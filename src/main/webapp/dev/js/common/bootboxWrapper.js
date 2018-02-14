/* global bootbox:false */

import {
    BootstrapContextualColors,
} from './const';

/**
 * Wrapper for Bootbox.js (available at http://bootboxjs.com/)
 * "Bootbox.js is a small JavaScript library which allows you to create programmatic dialog boxes using
 *  Bootstrap modals"
 */

const DEFAULT_OK_TEXT = 'OK';
const DEFAULT_CANCEL_TEXT = 'Cancel';
const DEFAULT_YES_TEXT = 'Yes';
const DEFAULT_NO_TEXT = 'No';

function applyStyleToModal(modal, bootstrapContextualColors) {
    modal.find('.modal-header').addClass(`alert-${bootstrapContextualColors || BootstrapContextualColors.DEFAULT}`)
            .find('.modal-title').addClass(`icon-${bootstrapContextualColors || BootstrapContextualColors.DEFAULT}`);
}

/**
 * Custom alert dialog to replace default alert() function
 * Required params: titleText and messageText
 * Optional params: okButtonText (defaults to "OK")
 *                  bootstrapContextualColors (defaults to BootstrapContextualColors.DEFAULT)
 */
function showModalAlert(titleText, messageText, okButtonText, bootstrapContextualColors) {
    const modal = bootbox.dialog({
        title: titleText,
        message: messageText,
        buttons: {
            okay: {
                label: okButtonText || DEFAULT_OK_TEXT,
                className: `modal-btn-ok btn-${bootstrapContextualColors || BootstrapContextualColors.DEFAULT}`,
            },
        },
    });
    applyStyleToModal(modal, bootstrapContextualColors);
}

/**
 * Custom confirmation dialog to replace default confirm() function
 * Required params: titleText, messageText and okCallback
 * Optional params: cancelCallBack (defaults to null)
 *                  okButtonText (defaults to "OK")
 *                  cancelButtonText (defaults to "Cancel")
 *                  bootstrapContextualColors (defaults to BootstrapContextualColors.INFO)
 */
function showModalConfirmation(titleText, messageText, okCallback, cancelCallback,
        okButtonText, cancelButtonText, bootstrapContextualColors) {
    const modal = bootbox.dialog({
        title: titleText,
        message: messageText,
        buttons: {
            cancel: {
                label: cancelButtonText || DEFAULT_CANCEL_TEXT,
                className: 'modal-btn-cancel btn-default',
                callback: cancelCallback || null,
            },
            ok: {
                label: okButtonText || DEFAULT_OK_TEXT,
                className: `modal-btn-ok btn-${bootstrapContextualColors || BootstrapContextualColors.DEFAULT}`,
                callback: okCallback,
            },
        },
    });
    applyStyleToModal(modal, bootstrapContextualColors);
}

/**
 * Custom confirmation dialog to replace default confirm() function
 * Required params: titleText, messageText, yesButtonCallback and noButtonCallback
 * Optional params: cancelButtonCallBack (defaults to null)
 *                  yesButtonText (defaults to "Yes")
 *                  noButtonText (defaults to "No")
 *                  canelButtonText (defaults to "Cancel")
 *                  bootstrapContextualColors (defaults to BootstrapContextualColors.INFO)
 */
function showModalConfirmationWithCancel(titleText, messageText, yesButtonCallback, noButtonCallback,
        cancelButtonCallback, yesButtonText, noButtonText, cancelButtonText, bootstrapContextualColors) {
    const modal = bootbox.dialog({
        title: titleText,
        message: messageText,
        buttons: {
            yes: {
                label: yesButtonText || DEFAULT_YES_TEXT,
                className: `modal-btn-ok btn-${bootstrapContextualColors || BootstrapContextualColors.DEFAULT}`,
                callback: yesButtonCallback,
            },
            no: {
                label: noButtonText || DEFAULT_NO_TEXT,
                className: `modal-btn-ok btn-${bootstrapContextualColors || BootstrapContextualColors.DEFAULT}`,
                callback: noButtonCallback,
            },
            cancel: {
                label: cancelButtonText || DEFAULT_CANCEL_TEXT,
                className: 'modal-btn-cancel btn-default',
                callback: cancelButtonCallback || null,
            },
        },
    });
    applyStyleToModal(modal, bootstrapContextualColors);
}

export {
    showModalAlert,
    showModalConfirmation,
    showModalConfirmationWithCancel,
};
