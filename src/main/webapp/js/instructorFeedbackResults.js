/*

    InstructorFeedbackResults.js

*/


//Show/hide stats
function showHideStats(){
    if($("#show-stats-checkbox").is(":checked")){
        $(".resultStatistics").show();
    } else {
        $(".resultStatistics").hide();
    }
}

//Search functionality

function filterResults(searchText){
    var element = $("#frameBodyWrapper").children("div.panel");

    if($(element).parents(".resultStatistics").length){
        return;
    }

    for(var i = 0 ; i < element.length ; i++){
        var elm = element[i];
        var heading = $(elm).children('.panel-heading');
        if($(heading[0]).text().toLowerCase().indexOf(searchText.toLowerCase()) != -1){
           $(elm).show();
        } else {
           $(elm).hide();
        } 
    }
}

function updateResultsFilter(){
    filterResults($("#results-search-box").val());
}

function toggleCollapse(e, panels){
    if($(e).html().indexOf("Expand") != -1){
        panels = panels || $("div.panel-collapse");
        isExpandingAll = true;
        var i = 0;
        for(var idx = 0; idx < panels.length; idx++){
            if($(panels[idx]).attr('class').indexOf('in') == -1){
                setTimeout(showSingleCollapse, 100 * i, panels[idx]);
                i++;
            }
        }
        var htmlString = $(e).html();
        htmlString = htmlString.replace("Expand", "Collapse");
        $(e).html(htmlString);
    } else {
        panels = panels || $("div.panel-collapse");
        isCollapsingAll = true;
        var i = 0;
        for(var idx = 0; idx < panels.length; idx++){
            if($(panels[idx]).attr('class').indexOf('in') != -1){
                setTimeout(hideSingleCollapse, 100 * i, panels[idx]);
                i++;
            }
        }
        var htmlString = $(e).html();
        htmlString = htmlString.replace("Collapse", "Expand");
        $(e).html(htmlString);
    }
}

function showSingleCollapse(e){
    var heading = $(e).parent().children('.panel-heading');
    var glyphIcon = $(heading[0]).find('.glyphicon');
    $(glyphIcon[0]).removeClass('glyphicon-chevron-down');
    $(glyphIcon[0]).addClass('glyphicon-chevron-up');
    $(e).collapse("show");
}

function hideSingleCollapse(e){
    var heading = $(e).parent().children('.panel-heading');
    var glyphIcon = $(heading[0]).find('.glyphicon');
    $(glyphIcon[0]).removeClass('glyphicon-chevron-up');
    $(glyphIcon[0]).addClass('glyphicon-chevron-down');
    $(e).collapse("hide");
}

function toggleSingleCollapse(e){
    if(e.target == e.currentTarget){
        var glyphIcon = $(this).find('.glyphicon');
        var className = $(glyphIcon[0]).attr('class');
        if(className.indexOf('glyphicon-chevron-up') != -1){
            hideSingleCollapse($(e.target).attr('data-target'));
        } else {
            showSingleCollapse($(e.target).attr('data-target'));
        }
    }
}

function getNextId(e){
    var id = $(e).attr('id');
    var nextId = "#panelBodyCollapse-" + (parseInt(id.split('-')[1]) + 1);
    return nextId;
}

function bindCollapseEvents(panels, numPanels){
    for(var i=0 ; i<panels.length ; i++){
        var heading = $(panels[i]).children(".panel-heading");
        var bodyCollapse = $(panels[i]).children(".panel-collapse");
        if(heading.length != 0 && bodyCollapse.length != 0){
            numPanels++;
            //$(heading[0]).attr("data-toggle","collapse");
            //Use this instead of the data-toggle attribute to let [more/less] be clicked without collapsing panel
            if($(heading[0]).attr('class') == 'panel-heading'){
                $(heading[0]).click(toggleSingleCollapse);
            }
            $(heading[0]).attr("data-target","#panelBodyCollapse-"+numPanels);
            $(heading[0]).css("cursor", "pointer");
            $(bodyCollapse[0]).attr('id', "panelBodyCollapse-"+numPanels);
        }
    }

    $(".glyphicon").click(function(){
        var parent = $(this).parent();
        if(parent.length){
            $(parent).trigger('click');
        }
    });

    return numPanels;
}

window.onload = function(){
    var panels = $("div.panel");
    var numPanels = 0;

    bindCollapseEvents(panels, numPanels);
    $("a[id^='collapse-panels-button-section-'],a[id^='collapse-panels-button-team-']").on('click', function(){
        var panels = $(this).parent().children('div.panel').children('.panel-collapse');
        toggleCollapse(this, panels);
    });
};

//Set on ready events
$(document).ready(function(){
    $("#results-search-box").keyup(function(e){
        updateResultsFilter();
    });
    //prevent submitting form when enter is pressed.
    $("#results-search-box").keypress(function(e) {
        if(e.which == 13) {
            return false;
        }
    });

    //Show/Hide statistics
    showHideStats();
    $("#show-stats-checkbox").change(showHideStats);
});
