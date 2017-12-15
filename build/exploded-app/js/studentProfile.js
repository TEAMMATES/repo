!function(t){function e(n){if(o[n])return o[n].exports;var i=o[n]={i:n,l:!1,exports:{}};return t[n].call(i.exports,i,i.exports,e),i.l=!0,i.exports}var o={};e.m=t,e.c=o,e.i=function(t){return t},e.d=function(t,o,n){e.o(t,o)||Object.defineProperty(t,o,{configurable:!1,enumerable:!0,get:n})},e.n=function(t){var o=t&&t.__esModule?function(){return t.default}:function(){return t};return e.d(o,"a",o),o},e.o=function(t,e){return Object.prototype.hasOwnProperty.call(t,e)},e.p="",e(e.s=109)}({0:function(t,e,o){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var n={SUCCESS:"success",INFO:"info",WARNING:"warning",DANGER:"danger",PRIMARY:"primary",isValidType:function(t){return t===n.SUCCESS||t===n.INFO||t===n.PRIMARY||t===n.WARNING||t===n.DANGER}};n.DEFAULT=n.INFO;var i={COURSE_ID:"courseid",COURSE_NAME:"coursename",COURSE_TIME_ZONE:"coursetimezone",FEEDBACK_SESSION_NAME:"fsname",FEEDBACK_SESSION_STARTDATE:"startdate",FEEDBACK_SESSION_STARTTIME:"starttime",FEEDBACK_SESSION_TIMEZONE:"timezone",FEEDBACK_SESSION_VISIBLEDATE:"visibledate",FEEDBACK_SESSION_VISIBLETIME:"visibletime",FEEDBACK_SESSION_PUBLISHDATE:"publishdate",FEEDBACK_SESSION_PUBLISHTIME:"publishtime",FEEDBACK_SESSION_SESSIONVISIBLEBUTTON:"sessionVisibleFromButton",FEEDBACK_SESSION_RESULTSVISIBLEBUTTON:"resultsVisibleFromButton",FEEDBACK_QUESTION_CONSTSUMOPTION:"constSumOption",FEEDBACK_QUESTION_CONSTSUMOPTIONTABLE:"constSumOptionTable",FEEDBACK_QUESTION_CONSTSUMPOINTS:"constSumPoints",FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION:"constSumPointsForEachOption",FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT:"constSumPointsForEachRecipient",FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS:"constSumToRecipients",FEEDBACK_QUESTION_MCQCHOICE:"mcqOption",FEEDBACK_QUESTION_MSQCHOICE:"msqOption",FEEDBACK_QUESTION_NUMBEROFCHOICECREATED:"noofchoicecreated",FEEDBACK_QUESTION_NUMSCALE_MIN:"numscalemin",FEEDBACK_QUESTION_NUMSCALE_MAX:"numscalemax",FEEDBACK_QUESTION_NUMSCALE_STEP:"numscalestep",FEEDBACK_QUESTION_RANKOPTION:"rankOption",FEEDBACK_QUESTION_RANKOPTIONTABLE:"rankOptionTable",FEEDBACK_QUESTION_RANKTORECIPIENTS:"rankToRecipients",FEEDBACK_QUESTION_SHOWRESPONSESTO:"showresponsesto",FEEDBACK_QUESTION_SHOWGIVERTO:"showgiverto",FEEDBACK_QUESTION_SHOWRECIPIENTTO:"showrecipientto",FEEDBACK_QUESTION_TEXT:"questiontext",FEEDBACK_QUESTION_TYPE:"questiontype",FEEDBACK_QUESTION_EDITTEXT:"questionedittext",FEEDBACK_QUESTION_EDITTYPE:"questionedittype",FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE:"numofrecipientstype",FEEDBACK_QUESTION_RECIPIENTTYPE:"recipienttype",FEEDBACK_QUESTION_DESCRIPTION:"questiondescription",FEEDBACK_QUESTION_DISCARDCHANGES:"questiondiscardchanges",FEEDBACK_QUESTION_SAVECHANGESTEXT:"questionsavechangestext",FEEDBACK_SESSION_ENABLE_EDIT:"editsessiondetails"},a={ModalDialog:{UNREGISTERED_STUDENT:{header:"Register for TEAMMATES",text:"You have to register using a google account in order to access this page. Would you like to proceed and register?"}},StatusMessages:{INSTRUCTOR_DETAILS_LENGTH_INVALID:"Instructor Details must have 3 columns"}};e.Const=a,e.ParamsNames=i,e.StatusType=n},1:function(t,e,o){"use strict";function n(){return("ontouchstart"in window||window.DocumentTouch)&&document instanceof window.DocumentTouch}var i=o(3);String.prototype.includes||(String.prototype.includes=function(t,e){var o="number"==typeof e?e:0;return!(o+t.length>this.length)&&-1!==this.indexOf(t,o)}),$(document).on("click",".toggle-sort",function(t){var e=$(t.currentTarget),o=e.data("toggle-sort-comparator"),n=e.data("toggle-sort-extractor");(0,i.toggleSort)(e,o,n)}),$(document).on("ajaxComplete ready",function(){var t=$('[data-toggle="tooltip"]');t.tooltip({html:!0,container:"body"}),n()&&t.tooltip("disable"),$('span[data-toggle="tooltip"]').each(function(){$(this).text().replace(/\s/g,"")&&$(this).addClass("tool-tip-decorate")})})},109:function(t,e,o){o(58),t.exports=o(1)},11:function(t,e,o){"use strict";function n(t){$(document).on("click",t,function(t){function e(){window.location=o.attr("href")}t.preventDefault();var o=$(t.target),n=i.Const.ModalDialog.UNREGISTERED_STUDENT.header,r=i.Const.ModalDialog.UNREGISTERED_STUDENT.text;(0,a.showModalConfirmation)(n,r,e,null,null,null,i.StatusType.INFO)})}Object.defineProperty(e,"__esModule",{value:!0}),e.bindLinksInUnregisteredPage=void 0;var i=o(0),a=o(5);e.bindLinksInUnregisteredPage=n},12:function(t,e,o){"use strict";function n(t){for(var e=document.cookie.split("; ").map(function(t){return t.split("=")}),o=0;o<e.length;o+=1){var n=e[o][0],i=e[o][1];if(n===t)return i}return null}function i(){return"token="+n("token")}Object.defineProperty(e,"__esModule",{value:!0}),e.makeCsrfTokenParam=i},2:function(t,e,o){"use strict";function n(t){return!isNaN(Date.parse(t))}function i(t){return("string"==typeof t||"number"==typeof t)&&!isNaN(t-0)&&""!==t}function a(t){var e=$(t)[0],o=e.getBoundingClientRect(),n=$(window);return o.top>=-.25&&o.left>=-.25&&o.right<=n.width()+.25&&o.bottom<=n.height()+.25}Object.defineProperty(e,"__esModule",{value:!0}),e.isDate=n,e.isNumber=i,e.isWithinView=a},3:function(t,e,o){"use strict";function n(t,e){if(!(t instanceof e))throw new TypeError("Cannot call a class as a function")}function i(t){return null!==t&&void 0!==t}function a(t,e){var o=t;return-1!==o.indexOf("/")?-1!==o.indexOf("S")?201:202:"0%"===o?e?0:100:(o=o.replace("E","").replace("%",""),""===o?100:100+parseFloat(o))}function r(t,e,o,n,a,r){for(var s=0,l=[],E=$("tr",t),f=r;f<E.length;f+=1)if(void 0!==E[f].cells[e-1]){var p=i(n)?n:d.getDefaultExtractor(),S=$.trim(p($(E[f].cells[e-1])));l.push([S,E[f],f]),s=0!==s&&1!==s||!(0,u.isNumber)(S)?0!==s&&2!==s||!(0,u.isDate)(S)?3:2:1}var T=i(o)?o:c.getDefaultComparator(s);l.sort(function(t,e){var o=a?T(t[0].toUpperCase(),e[0].toUpperCase()):T(e[0].toUpperCase(),t[0].toUpperCase());return 0===o?t[2]-e[2]:o});var g=$(t.get(0)).children("tbody");g.size<1&&(g=t);for(var m=0;m<l.length;m+=1)g.get(0).appendChild(l[m][1]);l=null}function s(t,e,o){var n=t.hasClass("button-sort-ascending"),a=E.getEnclosingTable(t),s=E.getColumnPositionOfButton(t,1),l=i(e)?c[e]:null,u=i(o)?d[o]:null,f=!n;r(a,s,l,u,f,1),f?E.setButtonToSortedAscending(t):E.setButtonToSortedDescending(t)}Object.defineProperty(e,"__esModule",{value:!0}),e.toggleSort=e.getPointValue=e.Comparators=void 0;var l=function(){function t(t,e){for(var o=0;o<e.length;o++){var n=e[o];n.enumerable=n.enumerable||!1,n.configurable=!0,"value"in n&&(n.writable=!0),Object.defineProperty(t,n.key,n)}}return function(e,o,n){return o&&t(e.prototype,o),n&&t(e,n),e}}(),u=o(2),c=function(){function t(){n(this,t)}return l(t,null,[{key:"sortBase",value:function(t,e){return t<e?-1:t>e?1:0}},{key:"sortNum",value:function(t,e){return t-e}},{key:"sortDate",value:function(t,e){var o=Date.parse(t),n=Date.parse(e);return o>n?1:o<n?-1:0}},{key:"sortByPoints",value:function(e,o){var n=a(e,!0),i=a(o,!0);return(0,u.isNumber)(n)&&(0,u.isNumber)(i)?t.sortNum(n,i):t.sortBase(n,i)}},{key:"sortByDiff",value:function(e,o){var n=a(e,!1),i=a(o,!1);return(0,u.isNumber)(n)&&(0,u.isNumber)(i)?t.sortNum(n,i):t.sortBase(n,i)}},{key:"getDefaultComparator",value:function(e){return 1===e?t.sortNum:2===e?t.sortDate:t.sortBase}}]),t}(),d=function(){function t(){n(this,t)}return l(t,null,[{key:"textExtractor",value:function(t){return t.text()}},{key:"tooltipExtractor",value:function(t){return t.find("span").attr("data-original-title")}},{key:"dateStampExtractor",value:function(t){return t.data("dateStamp")}},{key:"getDefaultExtractor",value:function(){return t.textExtractor}}]),t}(),E=function(){function t(){n(this,t)}return l(t,null,[{key:"getEnclosingTable",value:function(t){return $(t.parents("table")[0])}},{key:"getColumnPositionOfButton",value:function(t,e){return t.parent().children().index(t)+e}},{key:"clearAllSortStates",value:function(t){t.find(".icon-sort").attr("class","icon-sort unsorted"),t.find(".button-sort-ascending").removeClass("button-sort-ascending").addClass("button-sort-none"),t.find(".button-sort-descending").removeClass("button-sort-descending").addClass("button-sort-none")}},{key:"setButtonToSortedAscending",value:function(t){this.clearAllSortStates(this.getEnclosingTable(t)),t.addClass("button-sort-ascending"),t.find(".icon-sort").attr("class","icon-sort sorted-ascending")}},{key:"setButtonToSortedDescending",value:function(t){this.clearAllSortStates(this.getEnclosingTable(t)),t.addClass("button-sort-descending"),t.find(".icon-sort").attr("class","icon-sort sorted-descending")}}]),t}();e.Comparators=c,e.getPointValue=a,e.toggleSort=s},4:function(t,e,o){"use strict";function n(t,e){void 0===e||null===e?$(window).scrollTop(t):$("html, body").animate({scrollTop:t},e)}function i(t,e){var o={type:"top",offset:0,duration:0},i=e||{},a=i.type||o.type,s=i.offset||o.offset,l=i.duration||o.duration,u="view"===a;if(!u||!(0,r.isWithinView)(t)){var c=$(".navbar")[0],d=c?c.offsetHeight:0,E=$("#footerComponent")[0],f=E?E.offsetHeight:0,p=window.innerHeight-d-f,S=p<t.offsetHeight,T=window.scrollY<t.offsetTop,g=!u||S||!T;void 0===i.offset&&(s=g?-1*d:-1*f),g||(s*=-1,s+=t.offsetHeight-window.innerHeight);n(t.offsetTop+s,l)}}function a(t){n(0,t)}Object.defineProperty(e,"__esModule",{value:!0}),e.scrollToTop=e.scrollToElement=void 0;var r=o(2);e.scrollToElement=i,e.scrollToTop=a},5:function(t,e,o){"use strict";function n(t,e){t.find(".modal-header").addClass("alert-"+(e||s.StatusType.DEFAULT)).find(".modal-title").addClass("icon-"+(e||s.StatusType.DEFAULT))}function i(t,e,o,i){n(bootbox.dialog({title:t,message:e,buttons:{okay:{label:o||l,className:"modal-btn-ok btn-"+(i||s.StatusType.DEFAULT)}}}),i)}function a(t,e,o,i,a,r,c){n(bootbox.dialog({title:t,message:e,buttons:{cancel:{label:r||u,className:"modal-btn-cancel btn-default",callback:i||null},ok:{label:a||l,className:"modal-btn-ok btn-"+(c||s.StatusType.DEFAULT),callback:o}}}),c)}function r(t,e,o,i,a,r,l,E,f){n(bootbox.dialog({title:t,message:e,buttons:{yes:{label:r||c,className:"modal-btn-ok btn-"+(f||s.StatusType.DEFAULT),callback:o},no:{label:l||d,className:"modal-btn-ok btn-"+(f||s.StatusType.DEFAULT),callback:i},cancel:{label:E||u,className:"modal-btn-cancel btn-default",callback:a||null}}}),f)}Object.defineProperty(e,"__esModule",{value:!0}),e.showModalConfirmationWithCancel=e.showModalConfirmation=e.showModalAlert=void 0;var s=o(0),l="OK",u="Cancel",c="Yes",d="No";e.showModalAlert=i,e.showModalConfirmation=a,e.showModalConfirmationWithCancel=r},58:function(t,e,o){"use strict";function n(){var t=$("#editableProfilePicture"),e=t.guillotine("getData"),o=t.prop("naturalWidth")*e.scale,n=t.prop("naturalHeight")*e.scale;$("#cropBoxLeftX").val(e.x),$("#cropBoxTopY").val(e.y),$("#cropBoxRightX").val(e.x+e.w),$("#cropBoxBottomY").val(e.y+e.h),$("#rotate").val(e.angle),$("#pictureWidth").val(o),$("#pictureHeight").val(n),$("#profilePictureEditForm").submit()}function i(){if(""!==$("#studentPhoto").val()){var t=$("#profileUploadPictureSubmit").html();$.ajax({url:"/page/studentProfileCreateFormUrl?"+(0,r.makeCsrfTokenParam)()+"&user="+$("input[name='user']").val(),beforeSend:function(){$("#profileUploadPictureSubmit").html('<img src="/images/ajax-loader.gif">')},error:function(){$("#profileUploadPictureSubmit").text(t),(0,l.setStatusMessage)("There seems to be a network error, please try again later",a.StatusType.DANGER),(0,s.scrollToTop)({duration:""})},success:function(e){e.isError?($("#profileUploadPictureSubmit").text(t),(0,l.setStatusMessage)("There seems to be a network error, please try again later",a.StatusType.DANGER),(0,s.scrollToTop)({duration:""})):($("#profilePictureUploadForm").attr("enctype","multipart/form-data"),$("#profilePictureUploadForm").attr("encoding","multipart/form-data"),$("#profilePictureUploadForm").attr("action",e.formUrl),$("#profilePictureUploadForm").submit())}})}}var a=o(0),r=o(12),s=o(4),l=o(8),u=o(11);$(document).ready(function(){(0,u.bindLinksInUnregisteredPage)("[data-unreg].navLinks"),$(".form-control").on("click",function(){$(this).val()===$(this).attr("data-actual-value")&&$(this).select()}),$("#profileUploadPictureSubmit").on("click",function(){i()}),$("#profileEditPictureSubmit").on("click",function(){n()}),$(window).load(function(){$("#studentPhoto").change(function(){var t=$(this).val();if(""===t)$("#profileUploadPictureSubmit").prop("disabled",!0),$(".filename-preview").val("No File Selected");else{$("#profileUploadPictureSubmit").prop("disabled",!1);var e=t.split("\\").pop().split("/").pop();$(".filename-preview").val(e)}});var t=$("#editableProfilePicture");0!==t.length&&(t.guillotine({width:150,height:150}),t.guillotine("fit"),$("#profilePicEditRotateLeft").click(function(){t.guillotine("rotateLeft")}),$("#profilePicEditZoomIn").click(function(){t.guillotine("zoomIn")}),$("#profilePicEditZoomOut").click(function(){t.guillotine("zoomOut")}),$("#profilePicEditRotateRight").click(function(){t.guillotine("rotateRight")}),$("#profilePicEditPanUp").click(function(){var e=t.guillotine("getData");t.guillotine("instance")._offset(e.x/e.w,(e.y-10)/e.h)}),$("#profilePicEditPanLeft").click(function(){var e=t.guillotine("getData");t.guillotine("instance")._offset((e.x-10)/e.w,e.y/e.h)}),$("#profilePicEditPanRight").click(function(){var e=t.guillotine("getData");t.guillotine("instance")._offset((e.x+10)/e.w,e.y/e.h)}),$("#profilePicEditPanDown").click(function(){var e=t.guillotine("getData");t.guillotine("instance")._offset(e.x/e.w,(e.y+10)/e.h)}),$("#pictureWidth").val(t.prop("naturalWidth")),$("#pictureHeight").val(t.prop("naturalHeight")),"true"===$("#profilePic").attr("data-edit")&&$("#studentPhotoUploader").modal({show:!0}))})})},8:function(t,e,o){"use strict";function n(t,e){var o=$(c),n=$("<div></div>"),i=l.StatusType.isValidType(e)?e:l.StatusType.INFO;return n.addClass("overflow-auto alert alert-"+i+" icon-"+i+" statusMessage"),n.html(t),o.empty(),o.append(n),o}function i(t,e){if(""!==t&&void 0!==t&&null!==t){var o=n(t,e);o.show(),(0,u.scrollToElement)(o[0],{offset:-window.innerHeight/2})}}function a(t,e,o){if(""!==t&&void 0!==t&&null!==t){var i=n(t,e).clone().show();$(c).remove(),$(o).prepend(i);var a={offset:-window.innerHeight/8,duration:1e3};(0,u.scrollToElement)(i[0],a)}}function r(t){var e=$(c);e.append($(t)),e.show()}function s(){var t=$(c);t.empty(),t.hide()}Object.defineProperty(e,"__esModule",{value:!0}),e.setStatusMessageToForm=e.setStatusMessage=e.clearStatusMessages=e.appendStatusMessage=void 0;var l=o(0),u=o(4),c="#statusMessagesToUser";e.appendStatusMessage=r,e.clearStatusMessages=s,e.setStatusMessage=i,e.setStatusMessageToForm=a}});