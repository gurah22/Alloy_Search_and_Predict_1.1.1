"use strict";

var SEARCH_PAGE = function() {
    var refreshElementDropdowns = function() {
        showTheRightnumberOfHandPickedElements();
        restrictToOneOfEachElement(0);
    }

    var showTheRightnumberOfHandPickedElements = function() {
        var elements = [
             $("#element1"),
             $("#element2"),
             $("#element3"),
             $("#element4"),
             $("#element5")
         ];

        var numberOfHandPickedElements = $("#numberOfHandPickedElements").val();

        var i;
        for (i = 0; i < elements.length; i++) {
            if (i < numberOfHandPickedElements) {
                elements[i].show();
            } else {
                elements[i].hide();
            }
        }
    }

    var get_options = function(id) {
        var all_options = [];
        $("#" + id + " option").each(function() {
            var current_option = $(this).val();
            all_options.push(current_option);
        });
        return all_options;
    }

    var get_remaining_options = function(dropdown_id) {
        var selected_option = $("#" + dropdown_id + " option:selected").val();

        var options = [];
        $("#" + dropdown_id + " option").each(function()
        {
            var current_option = $(this).val();
            if (current_option !== selected_option) {
                options.push(current_option);
            }
        });

        return options;
    }

    var replaceDropdownOptions = function(dropdown_id, values) {
        var dropdown = $("#" + dropdown_id);
        dropdown.empty();
        $.each(values, function(key, value) {
             dropdown
                 .append($("<option></option>")
                 .attr("value", value)
                 .text(value));
        });
    }

    var copyAllExceptSelection = function(from_dropdown_id, to_dropdown_id) {
        var to_dropdown = $("#" + to_dropdown_id);
        var selected_option = to_dropdown.val();
        var remaining_options = get_remaining_options(from_dropdown_id);
        replaceDropdownOptions(to_dropdown_id, remaining_options);

        var selected_option_is_still_available = $.inArray(selected_option, remaining_options) > 0;
        if (selected_option_is_still_available) {
            to_dropdown.val(selected_option);
        }
    }

    var restrictToOneOfEachElement = function(startingIndex) {
        var elements = [
             "element1",
             "element2",
             "element3",
             "element4",
             "element5"
         ];

        var i;
        for (i = startingIndex + 1; i < elements.length; i++) {
            var from_dropdown_id = elements[i - 1];
            var to_dropdown_id = elements[i];
            copyAllExceptSelection(from_dropdown_id, to_dropdown_id);
        }
    }

    var NUMBER_OF_AVAILABLE_ELEMENTS = 73;
    var required_number_of_element_combinations = function(number_of_wildcard_elements,
                                                           number_of_hand_picked_elements) {
        var number_of_choosable_elements = NUMBER_OF_AVAILABLE_ELEMENTS - number_of_hand_picked_elements;
        var number_of_element_combinations = math.combinations(number_of_choosable_elements, number_of_wildcard_elements);

        return number_of_element_combinations;
    }

    var required_number_of_calculations_per_combination = function(number_of_elements,
                                                                   step_size) {
        if (typeof number_of_elements !== "number"
            || typeof step_size !== "number") {
            console.debug("error in types of required_number_of_calculations")
        }

        var n = number_of_elements;
        var k = Math.round(1.0 / step_size) - number_of_elements;
        var number_of_calculations_per_element_combination = math.combinations(n + k - 1, k);
        return number_of_calculations_per_element_combination;
    }

    var required_number_of_calculations = function(number_of_wildcard_elements,
                                                   number_of_hand_picked_elements,
                                                   step_size) {
        var number_of_elements = number_of_wildcard_elements + number_of_hand_picked_elements;

        return required_number_of_element_combinations(number_of_wildcard_elements, number_of_hand_picked_elements)
            * required_number_of_calculations_per_combination(number_of_elements, step_size);
    }

    var round_to_value = function(value, round) {
        return round * Math.round(value / round);
    }

    var format_estimated_processing_time = function(seconds) {
        var seconds_label = " second/s";
        if (seconds <= 1) {
            return "1" + seconds_label;
        } else if (seconds < 10) {
            return "" + round_to_value(seconds, 1) + seconds_label;
        } else if (seconds < 30) {
            return "" + round_to_value(seconds, 5) + seconds_label;
        } else if (seconds < 50) {
            return "" + round_to_value(seconds, 10) + seconds_label;
        }

        var minutes = seconds / 60;
        var minutes_label = " minute/s";
        if (minutes < 5) {
            return "" + round_to_value(minutes, 1) + minutes_label;
        } else if (minutes < 30) {
            return "" + round_to_value(minutes, 5) + minutes_label;
        } else if (minutes < 50) {
            return "" + round_to_value(minutes, 10) + minutes_label;
        }

        var hours = minutes / 60;
        var hours_label = " hour/s";
        if (hours < 5) {
            return "" + round_to_value(hours, 1) + hours_label;
        } else if (hours < 30) {
            return "" + round_to_value(hours, 5) + hours_label;
        }

        return "" + round_to_value(hours, 10) + hours_label;
    }

    var CALCULATION_LIMIT;
    var setCalculationLimit = function setCalculationLimit(limit) {
        CALCULATION_LIMIT = limit;
    }

    var STEP_SIZE_OPTION_LIMIT = 1000;
    var ESTIMATED_SECONDS_PER_TRANSACTION = 0.0001;
    var update_step_size_dropdown = function() {
        var number_of_wildcard_elements = parseInt($("#numberOfWildcardElements").val());
        var number_of_hand_picked_elements = parseInt($("#numberOfHandPickedElements").val());
        var number_of_elements = number_of_wildcard_elements + number_of_hand_picked_elements;

        var dropdown = $("#stepSize");
        dropdown.empty();

        var i;
        for (i = number_of_elements; i <= STEP_SIZE_OPTION_LIMIT; ++i) {
            var step_size = 1.0 / i;
            var required_calculations = required_number_of_calculations(
                number_of_wildcard_elements,
                number_of_hand_picked_elements,
                step_size
            );

            if (required_calculations > CALCULATION_LIMIT) {
                break;
            }

            var step_size_percentage = Math.round(step_size * 10000) / 100;
            var estimated_processing_time = format_estimated_processing_time(required_calculations * ESTIMATED_SECONDS_PER_TRANSACTION);
            var number_of_result_rows = number_of_wildcard_elements > 0
                ? required_number_of_element_combinations(number_of_wildcard_elements, number_of_hand_picked_elements)
                : required_number_of_calculations_per_combination(number_of_hand_picked_elements, step_size);
            var label = ""
                + step_size_percentage.toFixed(2) + "%,"
                + " ~" + estimated_processing_time + ","
                + " " + number_of_result_rows + " available result row/s";

            dropdown
                 .append($("<option></option>")
                 .attr("value", step_size)
                 .text(label));
        }
    }

    return {
        setCalculationLimit: setCalculationLimit,
        refreshElementDropdowns: refreshElementDropdowns,
        restrictToOneOfEachElement: restrictToOneOfEachElement,
        update_step_size_dropdown: update_step_size_dropdown
    };
}();
