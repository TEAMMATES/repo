/**
 * If the incoming link to this page targets a panel in its
 * URL hash, e.g. "instructorHelp.jsp#question-essay",
 * then show the panel specified by the URL hash
 */
function syncPanelCollapseWithUrlHash() {
    if (window.location.hash) {
        const target = $('body').find(window.location.hash);
        if ($(target).hasClass('panel')) {
            const targetCollapse = $(target).find('.collapse');
            $(targetCollapse).collapse('show');
        }
    }
}

/**
 * Show the panel specified by the data-target when any
 * .collapse-link element is clicked.
 *
 * To be used by links on the instructorHelp.jsp page itself.
 */
function bindPanelCollapseLinksAndAnchor() {
    $('.collapse-link').on('click', function () {
        const targetPanel = this.getAttribute('data-target');
        $(targetPanel).collapse('show');
    });
}

$(document).ready(() => {
    syncPanelCollapseWithUrlHash();
    bindPanelCollapseLinksAndAnchor();
});
