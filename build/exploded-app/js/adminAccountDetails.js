!function(t){function e(o){if(n[o])return n[o].exports;var r=n[o]={i:o,l:!1,exports:{}};return t[o].call(r.exports,r,r.exports,e),r.l=!0,r.exports}var n={};e.m=t,e.c=n,e.i=function(t){return t},e.d=function(t,n,o){e.o(t,n)||Object.defineProperty(t,n,{configurable:!1,enumerable:!0,get:o})},e.n=function(t){var n=t&&t.__esModule?function(){return t.default}:function(){return t};return e.d(n,"a",n),n},e.o=function(t,e){return Object.prototype.hasOwnProperty.call(t,e)},e.p="",e(e.s=72)}({1:function(t,e,n){"use strict";function o(){return("ontouchstart"in window||window.DocumentTouch)&&document instanceof window.DocumentTouch}var r=n(3);String.prototype.includes||(String.prototype.includes=function(t,e){var n="number"==typeof e?e:0;return!(n+t.length>this.length)&&-1!==this.indexOf(t,n)}),$(document).on("click",".toggle-sort",function(t){var e=$(t.currentTarget),n=e.data("toggle-sort-comparator"),o=e.data("toggle-sort-extractor");(0,r.toggleSort)(e,n,o)}),$(document).on("ajaxComplete ready",function(){var t=$('[data-toggle="tooltip"]');t.tooltip({html:!0,container:"body"}),o()&&t.tooltip("disable"),$('span[data-toggle="tooltip"]').each(function(){$(this).text().replace(/\s/g,"")&&$(this).addClass("tool-tip-decorate")})})},2:function(t,e,n){"use strict";function o(t){return!isNaN(Date.parse(t))}function r(t){return("string"==typeof t||"number"==typeof t)&&!isNaN(t-0)&&""!==t}function a(t){var e=$(t)[0],n=e.getBoundingClientRect(),o=$(window);return n.top>=-.25&&n.left>=-.25&&n.right<=o.width()+.25&&n.bottom<=o.height()+.25}Object.defineProperty(e,"__esModule",{value:!0}),e.isDate=o,e.isNumber=r,e.isWithinView=a},21:function(t,e,n){"use strict";$(document).ready(function(){})},3:function(t,e,n){"use strict";function o(t,e){if(!(t instanceof e))throw new TypeError("Cannot call a class as a function")}function r(t){return null!==t&&void 0!==t}function a(t,e){var n=t;return-1!==n.indexOf("/")?-1!==n.indexOf("S")?201:202:"0%"===n?e?0:100:(n=n.replace("E","").replace("%",""),""===n?100:100+parseFloat(n))}function i(t,e,n,o,a,i){for(var u=0,s=[],d=$("tr",t),g=i;g<d.length;g+=1)if(void 0!==d[g].cells[e-1]){var p=r(o)?o:f.getDefaultExtractor(),v=$.trim(p($(d[g].cells[e-1])));s.push([v,d[g],g]),u=0!==u&&1!==u||!(0,c.isNumber)(v)?0!==u&&2!==u||!(0,c.isDate)(v)?3:2:1}var b=r(n)?n:l.getDefaultComparator(u);s.sort(function(t,e){var n=a?b(t[0].toUpperCase(),e[0].toUpperCase()):b(e[0].toUpperCase(),t[0].toUpperCase());return 0===n?t[2]-e[2]:n});var m=$(t.get(0)).children("tbody");m.size<1&&(m=t);for(var y=0;y<s.length;y+=1)m.get(0).appendChild(s[y][1]);s=null}function u(t,e,n){var o=t.hasClass("button-sort-ascending"),a=d.getEnclosingTable(t),u=d.getColumnPositionOfButton(t,1),s=r(e)?l[e]:null,c=r(n)?f[n]:null,g=!o;i(a,u,s,c,g,1),g?d.setButtonToSortedAscending(t):d.setButtonToSortedDescending(t)}Object.defineProperty(e,"__esModule",{value:!0}),e.toggleSort=e.getPointValue=e.Comparators=void 0;var s=function(){function t(t,e){for(var n=0;n<e.length;n++){var o=e[n];o.enumerable=o.enumerable||!1,o.configurable=!0,"value"in o&&(o.writable=!0),Object.defineProperty(t,o.key,o)}}return function(e,n,o){return n&&t(e.prototype,n),o&&t(e,o),e}}(),c=n(2),l=function(){function t(){o(this,t)}return s(t,null,[{key:"sortBase",value:function(t,e){return t<e?-1:t>e?1:0}},{key:"sortNum",value:function(t,e){return t-e}},{key:"sortDate",value:function(t,e){var n=Date.parse(t),o=Date.parse(e);return n>o?1:n<o?-1:0}},{key:"sortByPoints",value:function(e,n){var o=a(e,!0),r=a(n,!0);return(0,c.isNumber)(o)&&(0,c.isNumber)(r)?t.sortNum(o,r):t.sortBase(o,r)}},{key:"sortByDiff",value:function(e,n){var o=a(e,!1),r=a(n,!1);return(0,c.isNumber)(o)&&(0,c.isNumber)(r)?t.sortNum(o,r):t.sortBase(o,r)}},{key:"getDefaultComparator",value:function(e){return 1===e?t.sortNum:2===e?t.sortDate:t.sortBase}}]),t}(),f=function(){function t(){o(this,t)}return s(t,null,[{key:"textExtractor",value:function(t){return t.text()}},{key:"tooltipExtractor",value:function(t){return t.find("span").attr("data-original-title")}},{key:"dateStampExtractor",value:function(t){return t.data("dateStamp")}},{key:"getDefaultExtractor",value:function(){return t.textExtractor}}]),t}(),d=function(){function t(){o(this,t)}return s(t,null,[{key:"getEnclosingTable",value:function(t){return $(t.parents("table")[0])}},{key:"getColumnPositionOfButton",value:function(t,e){return t.parent().children().index(t)+e}},{key:"clearAllSortStates",value:function(t){t.find(".icon-sort").attr("class","icon-sort unsorted"),t.find(".button-sort-ascending").removeClass("button-sort-ascending").addClass("button-sort-none"),t.find(".button-sort-descending").removeClass("button-sort-descending").addClass("button-sort-none")}},{key:"setButtonToSortedAscending",value:function(t){this.clearAllSortStates(this.getEnclosingTable(t)),t.addClass("button-sort-ascending"),t.find(".icon-sort").attr("class","icon-sort sorted-ascending")}},{key:"setButtonToSortedDescending",value:function(t){this.clearAllSortStates(this.getEnclosingTable(t)),t.addClass("button-sort-descending"),t.find(".icon-sort").attr("class","icon-sort sorted-descending")}}]),t}();e.Comparators=l,e.getPointValue=a,e.toggleSort=u},72:function(t,e,n){n(21),t.exports=n(1)}});