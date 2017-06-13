/* global
FEEDBACK_QUESTION_NUMBEROFCHOICECREATED:false, FEEDBACK_QUESTION_MSQCHOICE:false

getQuestionNum: false
*/

function setMaxValForMaxSelectableChoicesInput(questionNum) {
    // don't count last div as it is the "add option button"
    const numberOfChoices = $(`#msqChoiceTable-${questionNum}`).children('div').length - 1;
    const $checkbox = $(`#msqEnableMaxSelectableChoices-${questionNum}`);

    if ($checkbox.prop('checked') && numberOfChoices > 1) {
        const $msqMaxSelectableChoices = $(`#msqMaxSelectableChoices-${questionNum}`);
        const val = $msqMaxSelectableChoices.val();

        $msqMaxSelectableChoices.prop('max', numberOfChoices);
        $msqMaxSelectableChoices.val(Math.min(numberOfChoices, val));
    }
}

function addMsqOption(questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const curNumberOfChoiceCreated =
            parseInt($(`#${FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(), 10);

    $(`
    <div id="msqOptionRow-${curNumberOfChoiceCreated}-${questionNum}">
        <div class="input-group">
            <span class="input-group-addon">
                <input type="checkbox" disabled>
            </span>
            <input type="text" name="${FEEDBACK_QUESTION_MSQCHOICE}-${curNumberOfChoiceCreated}"
                    id="${FEEDBACK_QUESTION_MSQCHOICE}-${curNumberOfChoiceCreated}-${questionNum}"
                    class="form-control msqOptionTextBox">
            <span class="input-group-btn">
                <button type="button" class="btn btn-default removeOptionLink" id="msqRemoveOptionLink"
                        onclick="removeMsqOption(${curNumberOfChoiceCreated}, ${questionNum})" tabindex="-1">
                    <span class="glyphicon glyphicon-remove"></span>
                </button>
            </span>
        </div>
    </div>
    `).insertBefore($(`#msqAddOptionRow-${questionNum}`));

    $(`#${FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(curNumberOfChoiceCreated + 1);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }

    setMaxValForMaxSelectableChoicesInput(questionNum);
}

function removeMsqOption(index, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const $thisRow = $(`#msqOptionRow-${index}-${questionNum}`);

    // count number of child rows the table have and - 1 because of add option button
    const numberOfOptions = $thisRow.parent().children('div').length - 1;

    if (numberOfOptions <= 1) {
        $thisRow.find('input').val('');
    } else {
        $thisRow.remove();

        if ($(questionId).attr('editStatus') === 'hasResponses') {
            $(questionId).attr('editStatus', 'mustDeleteResponses');
        }
    }

    setMaxValForMaxSelectableChoicesInput(questionNum);
}

function toggleMsqMaxSelectableChoices(questionNum) {
    const $checkbox = $(`#msqEnableMaxSelectableChoices-${questionNum}`);

    $(`#msqMaxSelectableChoices-${questionNum}`).prop('disabled', !$checkbox.prop('checked'));
    setMaxValForMaxSelectableChoicesInput(questionNum);
}

function removeMaxValForMaxSelectableChoicesInput(questionNum) {
    const $checkbox = $(`#msqEnableMaxSelectableChoices-${questionNum}`);

    if ($checkbox.prop('checked')) {
        const $msqMaxSelectableChoices = $(`#msqMaxSelectableChoices-${questionNum}`);

        $msqMaxSelectableChoices.removeProp('max');
    }
}

function toggleMsqGeneratedOptions(checkbox, questionNum) {
    if (checkbox.checked) {
        $(`#msqChoiceTable-${questionNum}`).find('input[type=text]').prop('disabled', true);
        $(`#msqChoiceTable-${questionNum}`).hide();
        $(`#msqGenerateForSelect-${questionNum}`).prop('disabled', false);
        $(`#msqOtherOptionFlag-${questionNum}`).closest('.checkbox').hide();
        $(`#generatedOptions-${questionNum}`).attr('value',
                                                   $(`#msqGenerateForSelect-${questionNum}`).prop('value'));
        removeMaxValForMaxSelectableChoicesInput(questionNum);
    } else {
        $(`#msqChoiceTable-${questionNum}`).find('input[type=text]').prop('disabled', false);
        $(`#msqChoiceTable-${questionNum}`).show();
        $(`#msqGenerateForSelect-${questionNum}`).prop('disabled', true);
        $(`#msqOtherOptionFlag-${questionNum}`).closest('.checkbox').show();
        $(`#generatedOptions-${questionNum}`).attr('value', 'NONE');
        setMaxValForMaxSelectableChoicesInput(questionNum);
    }
}

function toggleMsqOtherOptionEnabled(checkbox, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }
}

function changeMsqGenerateFor(questionNum) {
    $(`#generatedOptions-${questionNum}`).attr('value',
                                               $(`#msqGenerateForSelect-${questionNum}`).prop('value'));
}

$(document).ready(() => {
    $(document).on('change', 'input[name*="msqEnableMaxSelectableChoices"]', (e) => {
        const questionNumber = getQuestionNum($(e.target));

        toggleMsqMaxSelectableChoices(questionNumber);
    });
});

/* exported
addMsqOption, removeMsqOption, toggleMsqGeneratedOptions, changeMsqGenerateFor
*/
