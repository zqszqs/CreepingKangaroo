/**
 * User: Hongfei Zhou
 * Date: 14-1-17
 * JS Description:
 */

bearAPI.directive('hfDrawer', function () {
    return {
        restrict: 'EA',
        transclude: true,
        replace: true,
        template: "<div style='transition: max-height 0.6s; padding: 0 2% 0 2%; overflow: hidden; border: 0 insert; background-color: rgba(233, 218, 229, 0.6); box-shadow: 0 1px 1.5px rgba(124, 121, 127, 0.6);' ng-style='open && {\"max-height\": drawHeight} || !open && {\"max-height\": 0}'>" +
            "<div ng-transclude></div>" +
            "</div>",
        scope: {
            open: "=",
            drawHeight: "="
        }
    };
});

bearAPI.directive('hfButton', function () {
    return {
        restrict: 'EA',
        replace: true,
        scope: {
            onSubmit: '=',
            onDiscard: '=',
            param: '=',
            disable: '='
        },
        templateUrl: '/template/service/common/opbuttons'
    }
});

bearAPI.directive('hfLabel', function () {
    return {
        restrict: 'EA',
        replace: true,
        templateUrl: '/template/service/common/showlabels',
        scope: {
            labelValue: '@',
            labelLength: '='
        },
        link: function (scope, element) {
//            scope.hideCopy = function () {
//                if (typeof scope.hideCopyClock != 'undefined')
//                    clearTimeout(scope.hideCopyClock);
//                scope.hideCopyClock = setTimeout(function () {
//                    scope.showButtons = false;
//                    scope.$apply();
//                }, 500)
//            };
//            scope.clearHideCopy = function () {
//                if (typeof scope.hideCopyClock != 'undefined')
//                    clearTimeout(scope.hideCopyClock)
//            };
        }
    }
});

bearAPI.directive('hfBody', function () {
    return {
        restrict: 'EA',
        replace: true,
        scope: {
            bodyValue: "="
        },
        template: "<div><hf-json-object object-value='bodyParsedValue'></hf-json-object></div>",
        compile: function (element, attr, transclude) {
            return {
                pre: function (scope, element, attr) {
                    scope.bodyParsedValue = JSON.parse(scope.bodyValue);
                }
            }
        }
    }
});

var type = function (src) {
    if (src instanceof Array) return 'array';
    else if (src instanceof Object) return 'object';
    else return 'string';

};

bearAPI.directive('hfJsonArray', function ($compile) {
    return {
        restrict: 'EA',
        scope: {
            arrayValue: "="
        },
        replace: true,
        link: function (scope, element, attrs) {
            scope.fields = scope.arrayValue.map(function (entry) {
                return {type: type(entry), value: entry}
            });
            var template = "<div class='inline'>[</div><div class='indent'><div ng-repeat='f in fields'>" +
                "<div ng-switch='f.type' class='inline'>" +
                "<hf-json-field ng-switch-when='string' field-value='f.value'></hf-json-field>" +
                "<hf-json-object ng-switch-when='object' object-value='f.value'></hf-json-object>" +
                "<hf-json-array ng-switch-when='array' array-value='f.value'></hf-json-array>" +
                "</div> " +
                "</div> </div><div class='inline'>]</div> ";
            var $template = angular.element(template);
            $compile($template)(scope);
            element.append($template);
        }
    }
});

bearAPI.directive('hfJsonObject', function ($compile) {
    return {
        restrict: 'EA',
        replace: true,
        scope: {
            objectValue: '='
        },
        link: function (scope, element, attrs) {
            scope.fields = [];
            for (n in scope.objectValue) {
                scope.fields.push({name: n, type: type(scope.objectValue[n]), value: scope.objectValue[n]})
            }
            var template = "<div class='inline'>{</div><div class='indent'>" +
                "<div ng-repeat='f in fields'><div><div class='inline' style='vertical-align: top'>\"{{f.name}}\": </div>" +
                "<div ng-switch='f.type' class='inline'>" +
                "<hf-json-field ng-switch-when='string' field-value='f.value' ng-class='inline'></hf-json-field>" +
                "<hf-json-object ng-switch-when='object' object-value='f.value'></hf-json-object>" +
                "<hf-json-array ng-switch-when='array' array-value='f.value'></hf-json-array>" +
                "</div> " +
                "</div></div> </div><div class='inline'>}</div>";
            var $template = angular.element(template);
            $compile($template)(scope);
            element.append($template);
        }
    };
});

bearAPI.directive('hfJsonField', function () {
    return {
        restrict: 'EA',
        scope: {
            fieldValue: "="
        },
        template: '<div class="inline">{{fieldParsedValue}}</div><div class="inline" ng-repeat="prop in properties"><div class="inline body_prop" ng-class="{body_error: prop.error, body_warning: prop.warning}">{{prop.message}}</div></div>',
        compile: function (element, attrs) {
            return {
                pre: function (scope, element, attrs) {
                    var value, type;
                    if (typeof scope.fieldValue != 'string') {
                        value = scope.fieldValue;
                    } else {
                        var lists = scope.fieldValue.split("&&");

                        var errorList = [];
                        var warningList = [];

                        lists.forEach(function (entry) {
                            var nameValue = entry.split("=");
                            if (nameValue[0] == 'value' || nameValue[0] == 'VALUE') value = nameValue[1];
                            else if (nameValue[0] == 'type' || nameValue[0] == 'TYPE') type = nameValue[1];
                            else if (nameValue[0] == 'error' || nameValue[0] == 'ERROR') errorList.push({error: true, message: nameValue[1]})
                            else if (nameValue[0] == 'warning' || nameValue[0] == 'WARNING') warningList.push({warning: true, message: nameValue[1]})
                        });
                        scope.properties = errorList.concat(warningList);
                    }

                    if (typeof value == 'undefined') {
                        value = scope.fieldValue;
                        if (typeof value == 'string')
                            type = 'STRING';
                    }

                    if (type == 'STRING')
                        scope.fieldParsedValue = '"' + value + '"';
                    else
                        scope.fieldParsedValue = value;
                }
            }
        }
    }
});

bearAPI.directive('draggable', function () {
    return {
        restrict: 'EA',
        scope: {
            onDragStart: '=',
            onDragEnd: '='
        },
        link: function (scope, element, attrs) {
            element[0].addEventListener('drop', scope.onDragStart, false);
            element[0].addEventListener('dragend', scope.onDragEnd, false);
        }
    }
});

bearAPI.directive('droppable', function() {
    return {
        restrict: 'EA',
        scope: {
            onDrop: '=',
            onDragOver: '='
        },
        link: function (scope, element, attrs) {
            element[0].addEventListener('drop', scope.onDrop, false);
            element[0].addEventListener('dragover', scope.onDragOver, false);
        }
    }
});