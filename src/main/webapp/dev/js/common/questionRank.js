import {
    ParamsNames,
} from './const';

function isRankOptionsQuestion(qnNumber) {
    return $(`#form_editquestion-${qnNumber}`).children('input[name="questiontype"]').val() === 'RANK_OPTIONS';
}

function isMinOptionsToBeRankedEnabled(qnNumber) {
    return isRankOptionsQuestion(qnNumber) ? $(`#minOptionsToBeRankedEnabled-${qnNumber}`).prop('checked')
            : $(`#minRecipientsToBeRankedEnabled-${qnNumber}`).prop('checked');
}

function isMaxOptionsToBeRankedEnabled(qnNumber) {
    return isRankOptionsQuestion(qnNumber) ? $(`#maxOptionsToBeRankedEnabled-${qnNumber}`).prop('checked')
            : $(`#maxRecipientsToBeRankedEnabled-${qnNumber}`).prop('checked');
}

function getNumOfRankOptions(qnNumber) {
    if (isRankOptionsQuestion(qnNumber)) {
        // for rank options question, return number of options
        return $(`#rankOptionRows-${qnNumber}`).children().length;
    }

    // for rank recipients question, compute the number of recipients
    const recipient = $(`#recipienttype-${qnNumber}`).val();

    switch (recipient) {
    case 'STUDENTS':
    case 'INSTRUCTORS':
    case 'TEAMS':
        return $(`#num-${recipient.toLowerCase()}`).val();
    case 'OWN_TEAM_MEMBERS':
    case 'OWN_TEAM_MEMBERS_INCLUDING_SELF':
        // returning infinite as this is dependent on team size
        return Number.MAX_SAFE_INTEGER;
    default:
        // other recipient types like NONE, SELF have only 1 recipient
        return 1;
    }
}

function getMinOptionsToBeRankedBox(qnNumber) {
    return isRankOptionsQuestion(qnNumber) ? $(`#minOptionsToBeRanked-${qnNumber}`)
            : $(`#minRecipientsToBeRanked-${qnNumber}`);
}

function getMaxOptionsToBeRankedBox(qnNumber) {
    return isRankOptionsQuestion(qnNumber) ? $(`#maxOptionsToBeRanked-${qnNumber}`)
            : $(`#maxRecipientsToBeRanked-${qnNumber}`);
}

function setUpperLimitForMinOptionsToBeRanked(qnNumber, upperLimit) {
    getMinOptionsToBeRankedBox(qnNumber).prop('max', upperLimit);
}

function setUpperLimitForMaxOptionsToBeRanked(qnNumber, upperLimit) {
    getMaxOptionsToBeRankedBox(qnNumber).prop('max', upperLimit);
}

function getMinOptionsToBeRanked(qnNumber) {
    if (isMinOptionsToBeRankedEnabled(qnNumber)) {
        return parseInt(getMinOptionsToBeRankedBox(qnNumber).val(), 10);
    }

    return Number.MAX_SAFE_INTEGER;
}

function getMaxOptionsToBeRanked(qnNumber) {
    if (isMaxOptionsToBeRankedEnabled(qnNumber)) {
        return parseInt(getMaxOptionsToBeRankedBox(qnNumber).val(), 10);
    }

    return Number.MAX_SAFE_INTEGER;
}

function setMinOptionsToBeRanked(qnNumber, newVal) {
    if (!isMinOptionsToBeRankedEnabled(qnNumber) || newVal < 1) {
        return;
    }

    getMinOptionsToBeRankedBox(qnNumber).val(newVal);
}

function setMaxOptionsToBeRanked(qnNumber, newVal) {
    if (!isMaxOptionsToBeRankedEnabled(qnNumber) || newVal < 1) {
        return;
    }

    getMaxOptionsToBeRankedBox(qnNumber).val(newVal);
}

function adjustMinOptionsToBeRanked(qnNumber) {
    if (!isMinOptionsToBeRankedEnabled(qnNumber)) {
        return;
    }

    const upperLimit = getNumOfRankOptions(qnNumber);
    const currentVal = Math.min(getMinOptionsToBeRanked(qnNumber), upperLimit);

    setUpperLimitForMinOptionsToBeRanked(qnNumber, upperLimit);
    setMinOptionsToBeRanked(qnNumber, currentVal);

    if (getMaxOptionsToBeRanked(qnNumber) < currentVal) {
        setMaxOptionsToBeRanked(qnNumber, currentVal);
    }
}

function adjustMaxOptionsToBeRanked(qnNumber) {
    if (!isMaxOptionsToBeRankedEnabled(qnNumber)) {
        return;
    }

    const upperLimit = getNumOfRankOptions(qnNumber);
    const currentVal = Math.min(getMaxOptionsToBeRanked(qnNumber), upperLimit);

    setUpperLimitForMaxOptionsToBeRanked(qnNumber, upperLimit);
    setMaxOptionsToBeRanked(qnNumber, currentVal);

    if (currentVal < getMinOptionsToBeRanked(qnNumber)) {
        setMinOptionsToBeRanked(qnNumber, currentVal);
    }
}

function adjustMinMaxOptionsToBeRanked(qnNumber) {
    adjustMaxOptionsToBeRanked(qnNumber);
    adjustMinOptionsToBeRanked(qnNumber);
}

function toggleMinOptionsToBeRanked(qnNumber) {
    const $maxOptionsToBeRanked = getMinOptionsToBeRankedBox(qnNumber);

    $maxOptionsToBeRanked.prop('disabled', !isMinOptionsToBeRankedEnabled(qnNumber));
    adjustMinMaxOptionsToBeRanked(qnNumber);
}

function toggleMaxOptionsToBeRanked(qnNumber) {
    const $maxOptionsToBeRanked = getMaxOptionsToBeRankedBox(qnNumber);

    $maxOptionsToBeRanked.prop('disabled', !isMaxOptionsToBeRankedEnabled(qnNumber));
    adjustMinMaxOptionsToBeRanked(qnNumber);
}

function addRankOption(questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const curNumberOfChoiceCreated =
            parseInt($(`#${ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(), 10);

    $(`
    <div class="margin-bottom-7px" id="rankOptionRow-${curNumberOfChoiceCreated}-${questionNum}">
        <div class="input-group width-100-pc">
            <span class="input-group-addon">
                <span class="glyphicon glyphicon-resize-vertical"></span>
            </span>
            <input type="text" name="${ParamsNames.FEEDBACK_QUESTION_RANKOPTION}-${curNumberOfChoiceCreated}"
                    id="${ParamsNames.FEEDBACK_QUESTION_RANKOPTION}-${curNumberOfChoiceCreated}-${questionNum}"
                    class="form-control rankOptionTextBox">
            <span class="input-group-btn">
                <button class="btn btn-default removeOptionLink" id="rankRemoveOptionLink"
                        onclick="removeRankOption(${curNumberOfChoiceCreated}, ${questionNum})" tabindex="-1">
                    <span class="glyphicon glyphicon-remove"></span>
                </button>
            </span>
        </div>
    </div>
    `).appendTo($(`#rankOptionRows-${questionNum}`));

    $(`#rankOptionRows-${questionNum}`).sortable('refresh');

    $(`#${ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}-${questionNum}`).val(curNumberOfChoiceCreated + 1);

    if ($(questionId).attr('editStatus') === 'hasResponses') {
        $(questionId).attr('editStatus', 'mustDeleteResponses');
    }

    adjustMinMaxOptionsToBeRanked(questionNum);
}

function showRankOptionTable(questionNum) {
    $(`#rankOptionTable-${questionNum}`).show();
}

function hideRankOptionTable(questionNum) {
    $(`#rankOptionTable-${questionNum}`).hide();
}

function removeRankOption(index, questionNum) {
    const questionId = `#form_editquestion-${questionNum}`;

    const $rankOptionRows = $(`#rankOptionRows-${questionNum}`);
    const $thisRow = $(`#rankOptionRow-${index}-${questionNum}`);

    // count number of child rows the table has
    const numberOfOptions = $rankOptionRows.children('div').length;

    if (numberOfOptions <= 2) {
        $thisRow.find('input').val('');
    } else {
        $thisRow.remove();

        if ($(questionId).attr('editStatus') === 'hasResponses') {
            $(questionId).attr('editStatus', 'mustDeleteResponses');
        }
    }

    adjustMinMaxOptionsToBeRanked(questionNum);
}

/**
 * Enables options for rank questions to be reordered through a drag and drop mechanism.
 * Binds an update event to the option elements which is triggered whenever the order of
 * elements changes. The event handler updates the ids of elements to match the new order.
 */
function makeRankOptionsReorderable(questionNum) {
    $(`#rankOptionRows-${questionNum}`).sortable({
        cursor: 'move',
        update() {
            $(this).children().each(function (index) {
                $(this).attr('id', `rankOptionRow-${index}-${questionNum}`);
                $(this).find('input[id^="rankOption-"]').attr({
                    name: `rankOption-${index}`,
                    id: `rankOption-${index}-${questionNum}`,
                });
                $(this).find('button[id="rankRemoveOptionLink"]')
                        .attr('onclick', `removeRankOption(${index},${questionNum})`);
            });
        },
    });
}

function bindRankEvents() {
    $(document).on('change', 'input[name="minOptionsToBeRanked"],input[name="minRecipientsToBeRanked"]', (e) => {
        const questionNum = $(e.currentTarget).closest('form').attr('data-qnnumber');
        adjustMinOptionsToBeRanked(questionNum);
    });

    $(document).on('change', 'input[name="maxOptionsToBeRanked"],input[name="maxRecipientsToBeRanked"]', (e) => {
        const questionNum = $(e.currentTarget).closest('form').attr('data-qnnumber');
        adjustMaxOptionsToBeRanked(questionNum);
    });

    $(document).on('change',
            'input[name="minOptionsToBeRankedEnabled"],input[name="minRecipientsToBeRankedEnabled"]', (e) => {
                const questionNum = $(e.currentTarget).closest('form').attr('data-qnnumber');
                toggleMinOptionsToBeRanked(questionNum);
            });

    $(document).on('change',
            'input[name="maxOptionsToBeRankedEnabled"],input[name="maxRecipientsToBeRankedEnabled"]', (e) => {
                const questionNum = $(e.currentTarget).closest('form').attr('data-qnnumber');
                toggleMaxOptionsToBeRanked(questionNum);
            });
}

function hideInvalidRankRecipientFeedbackPaths(qnNum) {
    const $form = $(`#form_editquestion-${qnNum}`);
    const qnType = $form.find('[name="questiontype"]').val();

    if (qnType === 'RANK_RECIPIENTS') {
        $form.find('[data-recipient-type="NONE"],[data-recipient-type="SELF"]').hide();
    } else {
        $form.find('[data-recipient-type="NONE"],[data-recipient-type="SELF"]').show();
    }
}

/**
 * Auto fills the min and max options to 1 if empty when the corresponding checkbox is checked
 * in Rank options and Rank recipients questions
 */
function bindAutoFillEmptyRankOptionsChangeEvent() {
    $(`[id^=${ParamsNames.FEEDBACK_QUESTION_RANK_IS_MAX_OPTIONS_TO_BE_RANKED_ENABLED}]`
            + `, [id^=${ParamsNames.FEEDBACK_QUESTION_RANK_IS_MIN_OPTIONS_TO_BE_RANKED_ENABLED}]`
            + `, [id^=${ParamsNames.FEEDBACK_QUESTION_RANK_IS_MIN_RECIPIENTS_TO_BE_RANKED_ENABLED}]`
            + `, [id^=${ParamsNames.FEEDBACK_QUESTION_RANK_IS_MAX_RECIPIENTS_TO_BE_RANKED_ENABLED}]`).change((e) => {
        const linkedInputId = $(e.currentTarget).data('linkedInputId');
        const linkedInput = $(`#${linkedInputId}`);

        if ($(e.currentTarget).prop('checked')) {
            if ($(linkedInput).val() === '') {
                $(linkedInput).val('1');
            }
        }
    });
}

export {
    addRankOption,
    bindRankEvents,
    hideInvalidRankRecipientFeedbackPaths,
    hideRankOptionTable,
    makeRankOptionsReorderable,
    removeRankOption,
    showRankOptionTable,
    toggleMaxOptionsToBeRanked,
    toggleMinOptionsToBeRanked,
    bindAutoFillEmptyRankOptionsChangeEvent,
};
