function _createForOfIteratorHelper(e,t){var n;if("undefined"==typeof Symbol||null==e[Symbol.iterator]){if(Array.isArray(e)||(n=_unsupportedIterableToArray(e))||t&&e&&"number"==typeof e.length){n&&(e=n);var s=0,i=function(){};return{s:i,n:function(){return s>=e.length?{done:!0}:{done:!1,value:e[s++]}},e:function(e){throw e},f:i}}throw new TypeError("Invalid attempt to iterate non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}var r,o=!0,a=!1;return{s:function(){n=e[Symbol.iterator]()},n:function(){var e=n.next();return o=e.done,e},e:function(e){a=!0,r=e},f:function(){try{o||null==n.return||n.return()}finally{if(a)throw r}}}}function _toConsumableArray(e){return _arrayWithoutHoles(e)||_iterableToArray(e)||_unsupportedIterableToArray(e)||_nonIterableSpread()}function _nonIterableSpread(){throw new TypeError("Invalid attempt to spread non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}function _unsupportedIterableToArray(e,t){if(e){if("string"==typeof e)return _arrayLikeToArray(e,t);var n=Object.prototype.toString.call(e).slice(8,-1);return"Object"===n&&e.constructor&&(n=e.constructor.name),"Map"===n||"Set"===n?Array.from(e):"Arguments"===n||/^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)?_arrayLikeToArray(e,t):void 0}}function _iterableToArray(e){if("undefined"!=typeof Symbol&&Symbol.iterator in Object(e))return Array.from(e)}function _arrayWithoutHoles(e){if(Array.isArray(e))return _arrayLikeToArray(e)}function _arrayLikeToArray(e,t){(null==t||t>e.length)&&(t=e.length);for(var n=0,s=new Array(t);n<t;n++)s[n]=e[n];return s}function _inherits(e,t){if("function"!=typeof t&&null!==t)throw new TypeError("Super expression must either be null or a function");e.prototype=Object.create(t&&t.prototype,{constructor:{value:e,writable:!0,configurable:!0}}),t&&_setPrototypeOf(e,t)}function _setPrototypeOf(e,t){return(_setPrototypeOf=Object.setPrototypeOf||function(e,t){return e.__proto__=t,e})(e,t)}function _createSuper(e){var t=_isNativeReflectConstruct();return function(){var n,s=_getPrototypeOf(e);if(t){var i=_getPrototypeOf(this).constructor;n=Reflect.construct(s,arguments,i)}else n=s.apply(this,arguments);return _possibleConstructorReturn(this,n)}}function _possibleConstructorReturn(e,t){return!t||"object"!=typeof t&&"function"!=typeof t?_assertThisInitialized(e):t}function _assertThisInitialized(e){if(void 0===e)throw new ReferenceError("this hasn't been initialised - super() hasn't been called");return e}function _isNativeReflectConstruct(){if("undefined"==typeof Reflect||!Reflect.construct)return!1;if(Reflect.construct.sham)return!1;if("function"==typeof Proxy)return!0;try{return Date.prototype.toString.call(Reflect.construct(Date,[],(function(){}))),!0}catch(e){return!1}}function _getPrototypeOf(e){return(_getPrototypeOf=Object.setPrototypeOf?Object.getPrototypeOf:function(e){return e.__proto__||Object.getPrototypeOf(e)})(e)}function _classCallCheck(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function _defineProperties(e,t){for(var n=0;n<t.length;n++){var s=t[n];s.enumerable=s.enumerable||!1,s.configurable=!0,"value"in s&&(s.writable=!0),Object.defineProperty(e,s.key,s)}}function _createClass(e,t,n){return t&&_defineProperties(e.prototype,t),n&&_defineProperties(e,n),e}(window.webpackJsonp=window.webpackJsonp||[]).push([[4],{MbNv:function(e,t,n){"use strict";n.d(t,"a",(function(){return B}));var s,i,r=n("lJxs"),o=n("ctOY"),a=n("vMQM"),u=n("R8lv"),c=n("fXoL"),l=n("mW1j"),p=n("jyEW"),h=n("PTBt"),m=function(){function e(){_classCallCheck(this,e)}return _createClass(e,[{key:"getQuestionCsvHeaders",value:function(){return["Feedback"]}},{key:"getMissingResponseCsvAnswers",value:function(){return[["No Response"]]}},{key:"populateQuestionStatistics",value:function(e,t){e.responses=t.allResponses.filter((function(e){return!e.isMissingResponse})).map((function(e){return e})),e.question=t.feedbackQuestion.questionDetails,e.recipientType=t.feedbackQuestion.recipientType,e.isStudent=!1}}]),e}(),f=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var s;return _classCallCheck(this,n),(s=t.call(this)).numOfConstSumOptions=2,s.constSumOptions=["",""],s.distributeToRecipients=!1,s.pointsPerOption=!1,s.forceUnevenDistribution=!1,s.distributePointsFor=u.b.NONE,s.points=100,s.questionText="",s.questionType=u.d.CONSTSUM_OPTIONS,s.numOfConstSumOptions=e.numOfConstSumOptions,s.constSumOptions=e.constSumOptions,s.pointsPerOption=e.pointsPerOption,s.forceUnevenDistribution=e.forceUnevenDistribution,s.distributePointsFor=e.distributePointsFor,s.points=e.points,s.questionText=e.questionText,s}return _createClass(n,[{key:"getQuestionCsvHeaders",value:function(){return["Feedback"].concat(_toConsumableArray(this.constSumOptions))}},{key:"getQuestionCsvStats",value:function(e){var t=[],n=new h.a(this);return this.populateQuestionStatistics(n,e),0===n.responses.length?[]:(n.calculateStatistics(),t.push(["Option","Total Points","Average Points","Points Received"]),Object.keys(n.pointsPerOption).sort().forEach((function(e){t.push([e,String(n.totalPointsPerOption[e]),String(n.averagePointsPerOption[e])].concat(_toConsumableArray(n.pointsPerOption[e].map(String))))})),t)}},{key:"isParticipantCommentsOnResponsesAllowed",value:function(){return!1}},{key:"isInstructorCommentsOnResponsesAllowed",value:function(){return!0}}]),n}(m),S=n("DL64"),v=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var s;return _classCallCheck(this,n),(s=t.call(this)).numOfConstSumOptions=0,s.constSumOptions=[],s.distributeToRecipients=!0,s.pointsPerOption=!1,s.forceUnevenDistribution=!1,s.distributePointsFor=u.b.NONE,s.points=100,s.questionText="",s.questionType=u.d.CONSTSUM_RECIPIENTS,s.numOfConstSumOptions=e.numOfConstSumOptions,s.constSumOptions=e.constSumOptions,s.pointsPerOption=e.pointsPerOption,s.forceUnevenDistribution=e.forceUnevenDistribution,s.distributePointsFor=e.distributePointsFor,s.points=e.points,s.questionText=e.questionText,s}return _createClass(n,[{key:"getQuestionCsvStats",value:function(e){var t=[],n=new S.a(this);return this.populateQuestionStatistics(n,e),0===n.responses.length?[]:(n.calculateStatistics(),t.push(["Team","Recipient","Total Points","Average Points","Points Received"]),Object.keys(n.pointsPerOption).sort().forEach((function(e){t.push([n.emailToTeamName[e],n.emailToName[e],String(n.totalPointsPerOption[e]),String(n.averagePointsPerOption[e])].concat(_toConsumableArray(n.pointsPerOption[e].map(String))))})),t)}},{key:"isParticipantCommentsOnResponsesAllowed",value:function(){return!1}},{key:"isInstructorCommentsOnResponsesAllowed",value:function(){return!0}}]),n}(m),d=n("768o"),y=n("RmXC"),C=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var s;return _classCallCheck(this,n),(s=t.call(this)).isNotSureAllowed=!0,s.questionText="",s.questionType=u.d.CONTRIB,s.isNotSureAllowed=e.isNotSureAllowed,s.questionText=e.questionText,s}return _createClass(n,[{key:"getQuestionCsvStats",value:function(e){var t=[],n=new d.a(this);if(this.populateQuestionStatistics(n,e),n.statistics=e.questionStatistics,n.parseStatistics(),!n.questionOverallStatistics||!n.questionOverallStatistics)return[];t.push(['In the points given below, an equal share is equal to 100 points. e.g. 80 means "Equal share - 20%" and 110 means "Equal share + 10%".']),t.push(["Claimed Contribution (CC) = the contribution claimed by the student."]),t.push(["Perceived Contribution (PC) = the average value of student's contribution as perceived by the team members."]),t.push(["Team","Name","Email","CC","PC","Ratings Received"]);for(var s=[],i=0,r=Object.keys(n.emailToName);i<r.length;i++){var o=r[i],a=n.emailToTeamName[o],u=n.emailToName[o],c=this.getContributionPointToText(n.questionOverallStatistics.results[o].claimed),l=this.getContributionPointToText(n.questionOverallStatistics.results[o].perceived),p=n.questionOverallStatistics.results[o].perceivedOthers.concat().sort((function(e,t){return t-e})).map(this.getContributionPointToText);0===p.length&&(p[0]="N/A"),s.push({teamName:a,name:u,email:o,claimedStr:c,perceivedStr:l,ratingsReceivedStr:p})}return s.sort((function(e,t){return e.teamName.localeCompare(t.teamName)||e.name.localeCompare(t.name)})),s.forEach((function(e){t.push([e.teamName,e.name,e.email,e.claimedStr,e.perceivedStr].concat(_toConsumableArray(e.ratingsReceivedStr)))})),t}},{key:"getContributionPointToText",value:function(e){return e===y.d?"Not Sure":e===y.c?"Not Submitted":e===y.b?"N/A":String(e)}},{key:"isParticipantCommentsOnResponsesAllowed",value:function(){return!1}},{key:"isInstructorCommentsOnResponsesAllowed",value:function(){return!1}}]),n}(m),T=n("jA8U"),g=function(e){_inherits(n,e);var t=_createSuper(n);function n(){return _classCallCheck(this,n),t.apply(this,arguments)}return _createClass(n,[{key:"getQuestionCsvStatsFrom",value:function(e,t){var n=[];return n.push(t?["Choice","Weight","Response Count","Percentage (%)","Weighted Percentage (%)"]:["Choice","Response Count","Percentage (%)"]),Object.keys(e.answerFrequency).sort().forEach((function(s){n.push(t?[s,String(e.weightPerOption[s]),String(e.answerFrequency[s]),String(e.percentagePerOption[s]),String(e.weightedPercentagePerOption[s])]:[s,String(e.answerFrequency[s]),String(e.percentagePerOption[s])])})),t?(n.push([],["Per Recipient Statistics"]),n.push(["Team","Recipient Name"].concat(_toConsumableArray(Object.keys(e.weightPerOption).map((function(t){return"".concat(t," [").concat(e.weightPerOption[t],"]")}))),["Total","Average"])),Object.keys(e.perRecipientResponses).sort().forEach((function(t){var s=e.perRecipientResponses[t];n.push([s.recipientTeam,s.recipient].concat(_toConsumableArray(Object.keys(e.weightPerOption).map((function(e){return String(s.responses[e])}))),[String(s.total),String(s.average)]))})),n):n}}]),n}(m),R=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var s;return _classCallCheck(this,n),(s=t.call(this)).hasAssignedWeights=!1,s.mcqWeights=[],s.mcqOtherWeight=0,s.numOfMcqChoices=0,s.mcqChoices=[],s.otherEnabled=!1,s.generateOptionsFor=u.c.NONE,s.questionText="",s.questionType=u.d.MCQ,s.hasAssignedWeights=e.hasAssignedWeights,s.mcqWeights=e.mcqWeights,s.mcqOtherWeight=e.mcqOtherWeight,s.numOfMcqChoices=e.numOfMcqChoices,s.mcqChoices=e.mcqChoices,s.otherEnabled=e.otherEnabled,s.generateOptionsFor=e.generateOptionsFor,s.questionText=e.questionText,s}return _createClass(n,[{key:"getQuestionCsvStats",value:function(e){var t=[],n=new T.a(this);return this.populateQuestionStatistics(n,e),0===n.responses.length?[]:(n.calculateStatistics(),t.push.apply(t,_toConsumableArray(this.getQuestionCsvStatsFrom(n,n.question.hasAssignedWeights))),t)}},{key:"isParticipantCommentsOnResponsesAllowed",value:function(){return!0}},{key:"isInstructorCommentsOnResponsesAllowed",value:function(){return!0}}]),n}(g),O=n("sBl4"),b=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var s;return _classCallCheck(this,n),(s=t.call(this)).msqChoices=[],s.otherEnabled=!1,s.generateOptionsFor=u.c.NONE,s.maxSelectableChoices=y.f,s.minSelectableChoices=y.f,s.hasAssignedWeights=!1,s.msqWeights=[],s.msqOtherWeight=0,s.questionText="",s.questionType=u.d.MSQ,s.msqChoices=e.msqChoices,s.otherEnabled=e.otherEnabled,s.generateOptionsFor=e.generateOptionsFor,s.maxSelectableChoices=e.maxSelectableChoices,s.minSelectableChoices=e.minSelectableChoices,s.hasAssignedWeights=e.hasAssignedWeights,s.msqWeights=e.msqWeights,s.msqOtherWeight=e.msqOtherWeight,s.questionText=e.questionText,s}return _createClass(n,[{key:"getQuestionCsvHeaders",value:function(){return["Feedback"].concat(_toConsumableArray(this.msqChoices))}},{key:"getQuestionCsvStats",value:function(e){var t=[],n=new O.a(this);return this.populateQuestionStatistics(n,e),n.calculateStatistics(),0!==n.responses.length&&n.hasAnswers?(t.push.apply(t,_toConsumableArray(this.getQuestionCsvStatsFrom(n,n.question.hasAssignedWeights))),t):[]}},{key:"isParticipantCommentsOnResponsesAllowed",value:function(){return!1}},{key:"isInstructorCommentsOnResponsesAllowed",value:function(){return!0}}]),n}(g),k=n("5HvB"),w=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var s;return _classCallCheck(this,n),(s=t.call(this)).minScale=1,s.maxScale=5,s.step=.5,s.questionText="",s.questionType=u.d.NUMSCALE,s.minScale=e.minScale,s.maxScale=e.maxScale,s.step=e.step,s.questionText=e.questionText,s}return _createClass(n,[{key:"getQuestionCsvStats",value:function(e){var t=[],n=new k.a(this);if(this.populateQuestionStatistics(n,e),0===n.responses.length)return[];n.calculateStatistics();var s=["Team","Recipient","Average","Minimum","Maximum"],i=this.shouldShowAverageExcludingSelfInCsvStats(e,n);i&&s.push("Average excluding self response"),t.push(s);var r,o=_createForOfIteratorHelper(Object.keys(n.teamToRecipientToScores).sort());try{for(o.s();!(r=o.n()).done;){var a,u=r.value,c=_createForOfIteratorHelper(Object.keys(n.teamToRecipientToScores[u]).sort());try{for(c.s();!(a=c.n()).done;){var l=a.value,p=n.teamToRecipientToScores[u][l],h=[u,l,String(p.average),String(p.min),String(p.max)];i&&h.push(String(p.averageExcludingSelf)),t.push(h)}}catch(m){c.e(m)}finally{c.f()}}}catch(m){o.e(m)}finally{o.f()}return t}},{key:"shouldShowAverageExcludingSelfInCsvStats",value:function(e,t){return e.feedbackQuestion.recipientType!==u.c.NONE&&Object.values(t.teamToRecipientToScores).some((function(e){return Object.values(e).some((function(e){return e.averageExcludingSelf}))}))}},{key:"isParticipantCommentsOnResponsesAllowed",value:function(){return!1}},{key:"isInstructorCommentsOnResponsesAllowed",value:function(){return!0}}]),n}(m),_=n("BWB/"),E=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var s;return _classCallCheck(this,n),(s=t.call(this)).minOptionsToBeRanked=y.f,s.maxOptionsToBeRanked=y.f,s.areDuplicatesAllowed=!1,s.options=[],s.questionText="",s.questionType=u.d.RANK_OPTIONS,s.minOptionsToBeRanked=e.minOptionsToBeRanked,s.maxOptionsToBeRanked=e.maxOptionsToBeRanked,s.areDuplicatesAllowed=e.areDuplicatesAllowed,s.options=e.options,s.questionText=e.questionText,s}return _createClass(n,[{key:"getQuestionCsvHeaders",value:function(){return["Feedback"].concat(_toConsumableArray(this.options.map((function(e,t){return"Rank "+(t+1)}))))}},{key:"getQuestionCsvStats",value:function(e){var t=[],n=new _.a(this);return this.populateQuestionStatistics(n,e),0===n.responses.length?[]:(n.calculateStatistics(),t.push(["Option","Overall Rank","Ranks Received"]),Object.keys(n.ranksReceivedPerOption).sort().forEach((function(e){t.push([e,n.rankPerOption[e]?String(n.rankPerOption[e]):""].concat(_toConsumableArray(n.ranksReceivedPerOption[e].map(String))))})),t)}},{key:"isParticipantCommentsOnResponsesAllowed",value:function(){return!1}},{key:"isInstructorCommentsOnResponsesAllowed",value:function(){return!0}}]),n}(m),N=n("TpVY"),I=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var s;return _classCallCheck(this,n),(s=t.call(this)).maxOptionsToBeRanked=y.f,s.minOptionsToBeRanked=y.f,s.areDuplicatesAllowed=!1,s.questionText="",s.questionType=u.d.RANK_RECIPIENTS,s.maxOptionsToBeRanked=e.maxOptionsToBeRanked,s.minOptionsToBeRanked=e.minOptionsToBeRanked,s.areDuplicatesAllowed=e.areDuplicatesAllowed,s.questionText=e.questionText,s}return _createClass(n,[{key:"getQuestionCsvStats",value:function(e){var t=[],n=new N.a(this);return this.populateQuestionStatistics(n,e),0===n.responses.length?[]:(n.calculateStatistics(),t.push(["Team","Recipient","Self Rank","Overall Rank","Overall Rank Excluding Self","Ranks Received","Team Rank","Team Rank Excluding Self"]),Object.keys(n.ranksReceivedPerOption).sort().forEach((function(e){t.push([n.emailToTeamName[e],n.emailToName[e],n.selfRankPerOption[e]?String(n.selfRankPerOption[e]):"-",n.rankPerOption[e]?String(n.rankPerOption[e]):"-",n.rankPerOptionExcludeSelf[e]?String(n.rankPerOptionExcludeSelf[e]):"-"].concat(_toConsumableArray(n.ranksReceivedPerOption[e].map(String))))})),t)}},{key:"isParticipantCommentsOnResponsesAllowed",value:function(){return!1}},{key:"isInstructorCommentsOnResponsesAllowed",value:function(){return!0}}]),n}(m),q=n("khBm"),A=n("4JDO"),P=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var s;return _classCallCheck(this,n),(s=t.call(this)).hasAssignedWeights=!1,s.numOfRubricChoices=0,s.rubricChoices=[],s.numOfRubricSubQuestions=0,s.rubricSubQuestions=[],s.rubricWeightsForEachCell=[],s.rubricDescriptions=[],s.questionText="",s.questionType=u.d.RUBRIC,s.hasAssignedWeights=e.hasAssignedWeights,s.numOfRubricChoices=e.numOfRubricChoices,s.rubricChoices=e.rubricChoices,s.numOfRubricSubQuestions=e.numOfRubricSubQuestions,s.rubricSubQuestions=e.rubricSubQuestions,s.rubricWeightsForEachCell=e.rubricWeightsForEachCell,s.rubricDescriptions=e.rubricDescriptions,s.questionText=e.questionText,s}return _createClass(n,[{key:"getQuestionCsvHeaders",value:function(){return["Sub Question","Choice Value","Choice Number"]}},{key:"getMissingResponseCsvAnswers",value:function(){return[["All Sub-Questions","No Response"]]}},{key:"getQuestionCsvStats",value:function(e){var t=this,n=[],s=new q.a(this);if(this.populateQuestionStatistics(s,e),0===s.responses.length)return[];s.calculateStatistics();var i=[""].concat(_toConsumableArray(s.choices));return s.hasWeights&&i.push("Average"),n.push(i),s.subQuestions.forEach((function(e,t){var i=["".concat(A.a.integerToLowerCaseAlphabeticalIndex(t+1),") ").concat(e)].concat(_toConsumableArray(s.choices.map((function(e,n){return"".concat(s.percentages[t][n],"% (").concat(s.answers[t][n],") ").concat(s.hasWeights?"[".concat(s.weights[t][n],"]"):"")}))));s.hasWeights&&i.push(String(s.subQuestionWeightAverage[t])),n.push(i)})),s.hasWeights?(n.push([],["Per Recipient Statistics"]),n.push(["Team","Recipient Name","Recipient's Email","Sub Question"].concat(_toConsumableArray(s.choices),["Total","Average"])),Object.values(s.perRecipientStatsMap).sort((function(e,t){return e.recipientTeam.localeCompare(t.recipientTeam)||e.recipientName.localeCompare(t.recipientName)})).forEach((function(e){t.rubricSubQuestions.forEach((function(t,i){n.push([e.recipientTeam,e.recipientName,e.recipientEmail?e.recipientEmail:"","".concat(A.a.integerToLowerCaseAlphabeticalIndex(i+1),") ").concat(t)].concat(_toConsumableArray(s.choices.map((function(t,n){return"".concat(e.percentages[i][n],"% (").concat(e.answers[i][n],") [").concat(s.weights[i][n],"]")}))),[String(e.subQuestionTotalChosenWeight[i]),String(e.subQuestionWeightAverage[i])]))}))})),n):n}},{key:"isParticipantCommentsOnResponsesAllowed",value:function(){return!1}},{key:"isInstructorCommentsOnResponsesAllowed",value:function(){return!0}}]),n}(m),F=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var s;return _classCallCheck(this,n),(s=t.call(this)).questionText="",s.questionType=u.d.TEXT,s.recommendedLength=e.recommendedLength,s.questionText=e.questionText,s.shouldAllowRichText=e.shouldAllowRichText,s}return _createClass(n,[{key:"getQuestionCsvStats",value:function(e){return[]}},{key:"isParticipantCommentsOnResponsesAllowed",value:function(){return!1}},{key:"isInstructorCommentsOnResponsesAllowed",value:function(){return!0}}]),n}(m),x=function(){function e(){_classCallCheck(this,e)}return _createClass(e,null,[{key:"fromApiOutput",value:function(e){switch(e.questionType){case u.d.CONSTSUM_OPTIONS:return new f(e);case u.d.CONSTSUM_RECIPIENTS:return new v(e);case u.d.CONTRIB:return new C(e);case u.d.MCQ:return new R(e);case u.d.MSQ:return new b(e);case u.d.NUMSCALE:return new w(e);case u.d.RANK_OPTIONS:return new E(e);case u.d.RANK_RECIPIENTS:return new I(e);case u.d.RUBRIC:return new P(e);case u.d.TEXT:return new F(e);default:throw new Error("Unknown question type: "+e.questionType)}}}]),e}(),Q=n("ayBD"),M=n("VtOQ"),D=n("eoFM"),U=((i=function(){function e(t){_classCallCheck(this,e),this.feedbackResponsesService=t}return _createClass(e,[{key:"getCsvForSessionResult",value:function(e,t,n,s,i){var r=this,o=[];if(o.push(["Course",e.feedbackSession.courseId]),o.push(["Session Name",e.feedbackSession.feedbackSessionName]),s&&o.push(["Section Name",s]),i){var a=new p.a;o.push(["Section View Detail",a.transform(i)])}this.generateEmptyRow(o),this.generateEmptyRow(o),e.questions.sort((function(e,t){return e.feedbackQuestion.questionNumber-t.feedbackQuestion.questionNumber}));var u,c=_createForOfIteratorHelper(e.questions);try{for(c.s();!(u=c.n()).done;){var l=u.value,h=JSON.parse(JSON.stringify(l));h.allResponses=h.allResponses.filter((function(e){return!s||!i||r.feedbackResponsesService.isFeedbackResponsesDisplayedOnSection(e,s,i)})),o.push.apply(o,_toConsumableArray(this.generateCsvRowsForQuestion(h,t,n)))}}catch(m){c.e(m)}finally{c.f()}return M.a.convertCsvContentsToCsvString(o)}},{key:"generateCsvRowsForQuestion",value:function(e,t,n){var s=[];if(s.push(["Question "+e.feedbackQuestion.questionNumber,e.feedbackQuestion.questionBrief]),this.generateEmptyRow(s),n){var i=this.getQuestionStats(e);i.length>0&&(s.push(["Summary Statistics"]),s.push.apply(s,_toConsumableArray(i)),this.generateEmptyRow(s),this.generateEmptyRow(s))}t||(e.allResponses=e.allResponses.filter((function(e){return!e.isMissingResponse})));var r=["Team","Giver's Full Name","Giver's Last Name","Giver's Email","Recipient's Team","Recipient's Full Name","Recipient's Last Name","Recipient's Email"].concat(_toConsumableArray(this.getQuestionSpecificHeaders(e.feedbackQuestion))),o=this.getIsParticipantCommentsOnResponsesAllowed(e.feedbackQuestion);o&&r.push("Giver's Comments");var a=this.getIsInstructorCommentsOnResponsesAllowed(e.feedbackQuestion);if(a)for(var u=e.allResponses.map((function(e){return e.instructorComments.length})).reduce((function(e,t){return Math.max(e,t)}),0),c=0;c<u;c+=1)r.push("Comment From","Comment");s.push(r),e.allResponses.sort((function(e,t){return e.giver.localeCompare(t.giver)||e.recipient.localeCompare(t.recipient)}));var l,p=_createForOfIteratorHelper(e.allResponses);try{for(p.s();!(l=p.n()).done;){var h,m=l.value,f=A.a.removeExtraSpace(m.giverTeam),S=A.a.removeExtraSpace(m.giver),v=m.giverLastName?A.a.removeExtraSpace(m.giverLastName):"",d=m.giverEmail?A.a.removeExtraSpace(m.giverEmail):"",y=A.a.removeExtraSpace(m.recipientTeam),C=A.a.removeExtraSpace(m.recipient),T=m.recipientLastName?A.a.removeExtraSpace(m.recipientLastName):"",g=m.recipientEmail?A.a.removeExtraSpace(m.recipientEmail):"",R=_createForOfIteratorHelper(m.isMissingResponse?this.getMissingResponseAnswers(e.feedbackQuestion):this.getResponseAnswers(m,e.feedbackQuestion));try{for(R.s();!(h=R.n()).done;){var O=h.value,b=[f,S,v,d,y,C,T,g].concat(_toConsumableArray(O));if(o){var k=m.participantComment?m.participantComment.commentText:"",w=A.a.getTextFromHtml(k),_=A.a.convertImageToLinkInHtml(k);b.push(w+_)}if(a){var E,N=_createForOfIteratorHelper(m.instructorComments);try{for(N.s();!(E=N.n()).done;){var I=E.value,q=I.commentGiverName?I.commentGiverName:"",P=I.commentText,F=A.a.getTextFromHtml(P),x=A.a.convertImageToLinkInHtml(P);b.push(q,F+x)}}catch(Q){N.e(Q)}finally{N.f()}}s.push(b)}}catch(Q){R.e(Q)}finally{R.f()}}}catch(Q){p.e(Q)}finally{p.f()}return this.generateEmptyRow(s),this.generateEmptyRow(s),s}},{key:"getQuestionStats",value:function(e){return x.fromApiOutput(e.feedbackQuestion.questionDetails).getQuestionCsvStats(e)}},{key:"getMissingResponseAnswers",value:function(e){return x.fromApiOutput(e.questionDetails).getMissingResponseCsvAnswers()}},{key:"getResponseAnswers",value:function(e,t){return Q.a.fromApiOutput(e.responseDetails).getResponseCsvAnswers(t.questionDetails)}},{key:"getQuestionSpecificHeaders",value:function(e){return x.fromApiOutput(e.questionDetails).getQuestionCsvHeaders()}},{key:"getIsParticipantCommentsOnResponsesAllowed",value:function(e){return x.fromApiOutput(e.questionDetails).isParticipantCommentsOnResponsesAllowed()}},{key:"getIsInstructorCommentsOnResponsesAllowed",value:function(e){return x.fromApiOutput(e.questionDetails).isInstructorCommentsOnResponsesAllowed()}},{key:"generateEmptyRow",value:function(e){e.push([])}}]),e}()).\u0275fac=function(e){return new(e||i)(c.ec(D.a))},i.\u0275prov=c.Mb({token:i,factory:i.\u0275fac,providedIn:"root"}),i),B=((s=function(){function e(t,n){_classCallCheck(this,e),this.httpRequestService=t,this.sessionResultCsvService=n}return _createClass(e,[{key:"getTemplateSessions",value:function(){return o}},{key:"getFeedbackSession",value:function(e){var t={intent:e.intent,courseid:e.courseId,fsname:e.feedbackSessionName};return e.key&&(t.key=e.key),e.moderatedPerson&&(t.moderatedperson=e.moderatedPerson),e.previewAs&&(t.previewas=e.previewAs),this.httpRequestService.get(a.d.SESSION,t)}},{key:"createFeedbackSession",value:function(e,t){return this.httpRequestService.post(a.d.SESSION,{courseid:e},t)}},{key:"updateFeedbackSession",value:function(e,t,n){return this.httpRequestService.put(a.d.SESSION,{courseid:e,fsname:t},n)}},{key:"deleteFeedbackSession",value:function(e,t){return this.httpRequestService.delete(a.d.SESSION,{courseid:e,fsname:t})}},{key:"getOngoingSessions",value:function(e,t){var n={starttime:String(e),endtime:String(t)};return this.httpRequestService.get(a.d.SESSIONS_ONGOING,n)}},{key:"getFeedbackSessionsForInstructor",value:function(e){var t;return t=null!=e?{entitytype:"instructor",courseid:e}:{entitytype:"instructor",isinrecyclebin:"false"},this.httpRequestService.get(a.d.SESSIONS,t)}},{key:"getFeedbackSessionsInRecycleBinForInstructor",value:function(){return this.httpRequestService.get(a.d.SESSIONS,{entitytype:"instructor",isinrecyclebin:"true"})}},{key:"getFeedbackSessionsForStudent",value:function(e){var t;return t=null!=e?{entitytype:"student",courseid:e}:{entitytype:"student"},this.httpRequestService.get(a.d.SESSIONS,t)}},{key:"hasResponsesForQuestion",value:function(e){return this.httpRequestService.get(a.d.HAS_RESPONSES,{entitytype:"instructor",questionid:e})}},{key:"hasStudentResponseForFeedbackSession",value:function(e,t){return this.httpRequestService.get(a.d.HAS_RESPONSES,{entitytype:"student",courseid:e,fsname:t})}},{key:"remindFeedbackSessionSubmissionForRespondents",value:function(e,t,n){return this.httpRequestService.post(a.d.SESSION_REMIND_SUBMISSION,{courseid:e,fsname:t},n)}},{key:"remindResultsLinkToRespondents",value:function(e,t,n){return this.httpRequestService.post(a.d.SESSION_REMIND_RESULT,{courseid:e,fsname:t},n)}},{key:"getFeedbackSessionSubmittedGiverSet",value:function(e){return this.httpRequestService.get(a.d.SESSION_SUBMITTED_GIVER_SET,{courseid:e.courseId,fsname:e.feedbackSessionName})}},{key:"publishFeedbackSession",value:function(e,t){return this.httpRequestService.post(a.d.SESSION_PUBLISH,{courseid:e,fsname:t})}},{key:"unpublishFeedbackSession",value:function(e,t){return this.httpRequestService.delete(a.d.SESSION_PUBLISH,{courseid:e,fsname:t})}},{key:"loadSessionStatistics",value:function(e,t){return this.httpRequestService.get(a.d.SESSION_STATS,{courseid:e,fsname:t})}},{key:"downloadSessionResults",value:function(e,t,n,s,i,o,a,u){var c=this;return this.getFeedbackSessionResults({courseId:e,feedbackSessionName:t,intent:n,questionId:o,groupBySection:a}).pipe(Object(r.a)((function(e){return c.sessionResultCsvService.getCsvForSessionResult(e,s,i,a,u)})))}},{key:"getFeedbackSessionResults",value:function(e){var t={courseid:e.courseId,fsname:e.feedbackSessionName,intent:e.intent};return e.questionId&&(t.questionid=e.questionId),e.groupBySection&&(t.frgroupbysection=e.groupBySection),e.key&&(t.key=e.key),this.httpRequestService.get(a.d.RESULT,t)}},{key:"moveSessionToRecycleBin",value:function(e,t){return this.httpRequestService.put(a.d.BIN_SESSION,{courseid:e,fsname:t})}},{key:"deleteSessionFromRecycleBin",value:function(e,t){return this.httpRequestService.delete(a.d.BIN_SESSION,{courseid:e,fsname:t})}},{key:"sendFeedbackSessionLinkToRecoveryEmail",value:function(e){return this.httpRequestService.post(a.d.SESSION_LINKS_RECOVERY,{studentemail:e.sessionLinksRecoveryEmail,captcharesponse:e.captchaResponse})}},{key:"isFeedbackSessionOpen",value:function(e){var t=Date.now();return t>=e.submissionStartTimestamp&&t<e.submissionEndTimestamp}},{key:"isFeedbackSessionPublished",value:function(e){return e.publishStatus===u.e.PUBLISHED}}]),e}()).\u0275fac=function(e){return new(e||s)(c.ec(l.a),c.ec(U))},s.\u0275prov=c.Mb({token:s,factory:s.\u0275fac,providedIn:"root"}),s)},ayBD:function(e,t,n){"use strict";n.d(t,"a",(function(){return v}));var s=n("R8lv"),i=function e(){_classCallCheck(this,e)},r=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var i;return _classCallCheck(this,n),(i=t.call(this)).answers=[],i.questionType=s.d.CONSTSUM,i.answers=e.answers,i}return _createClass(n,[{key:"getResponseCsvAnswers",value:function(e){return e.distributeToRecipients?[[this.answers.map(String).join("")]]:[[""].concat(_toConsumableArray(this.answers.map(String)))]}}]),n}(i),o=n("RmXC"),a=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var i;return _classCallCheck(this,n),(i=t.call(this)).answer=o.c,i.questionType=s.d.CONTRIB,i.answer=e.answer,i}return _createClass(n,[{key:"getResponseCsvAnswers",value:function(e){var t=this.answer;return[[t>100?"Equal share + ".concat(t-100,"%"):100===t?"Equal share":t>0?"Equal share - ".concat(100-t,"%"):0===t?"0%":t===o.d?"Not Sure":""]]}}]),n}(i),u=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var i;return _classCallCheck(this,n),(i=t.call(this)).answer="",i.isOther=!1,i.otherFieldContent="",i.questionType=s.d.MCQ,i.answer=e.answer,i.isOther=e.isOther,i.otherFieldContent=e.otherFieldContent,i}return _createClass(n,[{key:"getResponseCsvAnswers",value:function(e){var t=this.answer;return this.isOther&&(t=this.otherFieldContent),[[t]]}}]),n}(i),c=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var i;return _classCallCheck(this,n),(i=t.call(this)).answers=[],i.isOther=!1,i.otherFieldContent="",i.questionType=s.d.MSQ,i.answers=e.answers,i.isOther=e.isOther,i.otherFieldContent=e.otherFieldContent,i}return _createClass(n,[{key:"getResponseCsvAnswers",value:function(e){if(1===this.answers.length&&0===this.answers[0].length)return[[""]];var t,n=[],s=_createForOfIteratorHelper(e.msqChoices);try{for(s.s();!(t=s.n()).done;){var i=t.value;-1===this.answers.indexOf(i)?n.push(""):n.push(i)}}catch(r){s.e(r)}finally{s.f()}return[[""].concat(n)]}}]),n}(i),l=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var i;return _classCallCheck(this,n),(i=t.call(this)).answer=o.g,i.questionType=s.d.NUMSCALE,i.answer=e.answer,i}return _createClass(n,[{key:"getResponseCsvAnswers",value:function(e){var t=Math.round(1e3*(this.answer+Number.EPSILON))/1e3;return[[String(t)]]}}]),n}(i),p=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var i;return _classCallCheck(this,n),(i=t.call(this)).answers=[],i.questionType=s.d.RANK_OPTIONS,i.answers=e.answers,i}return _createClass(n,[{key:"getResponseCsvAnswers",value:function(e){for(var t=this,n=[],s=function(s){var i=t.answers.reduce((function(t,n,i){return n===s&&t.push(e.options[i]),t}),[]);n.push(i.join(", "))},i=1;i<=e.options.length;i+=1)s(i);return[[""].concat(n)]}}]),n}(i),h=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var i;return _classCallCheck(this,n),(i=t.call(this)).answer=o.i,i.questionType=s.d.RANK_RECIPIENTS,i.answer=e.answer,i}return _createClass(n,[{key:"getResponseCsvAnswers",value:function(e){return[[String(this.answer)]]}}]),n}(i),m=n("4JDO"),f=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var i;return _classCallCheck(this,n),(i=t.call(this)).answer=[],i.questionType=s.d.RUBRIC,i.answer=e.answer,i}return _createClass(n,[{key:"getResponseCsvAnswers",value:function(e){var t=[];return this.answer.forEach((function(n,s){var i=[],r=m.a.integerToLowerCaseAlphabeticalIndex(s+1),a="No Response",u="";n!==o.j&&(a=e.rubricChoices[n],u=String(n+1)),i.push(r),i.push(a),i.push(u),t.push(i)})),t}}]),n}(i),S=function(e){_inherits(n,e);var t=_createSuper(n);function n(e){var i;return _classCallCheck(this,n),(i=t.call(this)).answer="",i.questionType=s.d.TEXT,i.answer=e.answer,i}return _createClass(n,[{key:"getResponseCsvAnswers",value:function(e){return[[m.a.getTextFromHtml(this.answer)]]}}]),n}(i),v=function(){function e(){_classCallCheck(this,e)}return _createClass(e,null,[{key:"fromApiOutput",value:function(e){switch(e.questionType){case s.d.CONSTSUM:return new r(e);case s.d.CONTRIB:return new a(e);case s.d.MCQ:return new u(e);case s.d.MSQ:return new c(e);case s.d.NUMSCALE:return new l(e);case s.d.RANK_OPTIONS:return new p(e);case s.d.RANK_RECIPIENTS:return new h(e);case s.d.RUBRIC:return new f(e);case s.d.TEXT:return new S(e);default:throw new Error("Unknown question type: "+e.questionType)}}}]),e}()},ctOY:function(e){e.exports=JSON.parse('[{"name":"session using template: team peer evaluation","questions":[{"feedbackQuestionId":"","questionNumber":1,"questionBrief":"How much work did each team member contribute? (response will be shown anonymously to each team member). ","questionDescription":"","questionType":"CONTRIB","questionDetails":{"isNotSureAllowed":false},"giverType":"STUDENTS","recipientType":"OWN_TEAM_MEMBERS_INCLUDING_SELF","numberOfEntitiesToGiveFeedbackToSetting":"UNLIMITED","customNumberOfEntitiesToGiveFeedbackTo":1,"showResponsesTo":["INSTRUCTORS","RECIPIENT","GIVER_TEAM_MEMBERS"],"showGiverNameTo":["INSTRUCTORS"],"showRecipientNameTo":["INSTRUCTORS","RECIPIENT"]},{"feedbackQuestionId":"","questionNumber":2,"questionBrief":"What contributions did you make to the team? (response will be shown to each team member).","questionDescription":"","questionType":"TEXT","questionDetails":{},"giverType":"STUDENTS","recipientType":"SELF","numberOfEntitiesToGiveFeedbackToSetting":"UNLIMITED","customNumberOfEntitiesToGiveFeedbackTo":1,"showResponsesTo":["RECIPIENT","GIVER_TEAM_MEMBERS","INSTRUCTORS"],"showGiverNameTo":["RECIPIENT","GIVER_TEAM_MEMBERS","INSTRUCTORS"],"showRecipientNameTo":["RECIPIENT","GIVER_TEAM_MEMBERS","INSTRUCTORS"]},{"feedbackQuestionId":"","questionNumber":3,"questionBrief":"What comments do you have regarding each of your team members? (response is confidential and will only be shown to the instructor).","questionDescription":"","questionType":"TEXT","questionDetails":{},"giverType":"STUDENTS","recipientType":"OWN_TEAM_MEMBERS","numberOfEntitiesToGiveFeedbackToSetting":"UNLIMITED","customNumberOfEntitiesToGiveFeedbackTo":1,"showResponsesTo":["INSTRUCTORS"],"showGiverNameTo":["INSTRUCTORS"],"showRecipientNameTo":["INSTRUCTORS"]},{"feedbackQuestionId":"","questionNumber":4,"questionBrief":"How are the team dynamics thus far? (response is confidential and will only be to the instructor).","questionDescription":"","questionType":"TEXT","questionDetails":{},"giverType":"STUDENTS","recipientType":"OWN_TEAM","numberOfEntitiesToGiveFeedbackToSetting":"UNLIMITED","customNumberOfEntitiesToGiveFeedbackTo":1,"showResponsesTo":["INSTRUCTORS"],"showGiverNameTo":["INSTRUCTORS"],"showRecipientNameTo":["INSTRUCTORS"]},{"feedbackQuestionId":"","questionNumber":5,"questionBrief":"What feedback do you have for each of your team members? (response will be shown anonymously to each team member).","questionDescription":"","questionType":"TEXT","questionDetails":{},"giverType":"STUDENTS","recipientType":"OWN_TEAM_MEMBERS","numberOfEntitiesToGiveFeedbackToSetting":"UNLIMITED","customNumberOfEntitiesToGiveFeedbackTo":1,"showResponsesTo":["RECIPIENT","INSTRUCTORS"],"showGiverNameTo":["INSTRUCTORS"],"showRecipientNameTo":["RECIPIENT","INSTRUCTORS"]}]},{"name":"session with my own questions","questions":[]}]')},jyEW:function(e,t,n){"use strict";n.d(t,"a",(function(){return r}));var s=n("C58a"),i=n("fXoL"),r=function(){var e=function(){function e(){_classCallCheck(this,e)}return _createClass(e,[{key:"transform",value:function(e){switch(e){case s.a.BOTH:return"Show response only if both are in the selected section";case s.a.EITHER:return"Show response if either the giver or evaluee is in the selected section";case s.a.EVALUEE:return"Show response if the evaluee is in the selected section";case s.a.GIVER:return"Show response if the giver is in the selected section";default:return"Unknown"}}}]),e}();return e.\u0275fac=function(t){return new(t||e)},e.\u0275pipe=i.Pb({name:"sectionTypeDescription",type:e,pure:!0}),e}()}}]);