/* eslint-disable no-undef */

QUnit.module('instructorFeedbacks.js');

QUnit.test('extractQuestionNumFromEditFormId(id)', (assert) => {
    // Tests that extracting question number from form is correct.
    for (let i = 1; i < 1000; i++) {
        const id = `form_editquestion-${i}`;
        assert.equal(extractQuestionNumFromEditFormId(id), i);
    }
});
