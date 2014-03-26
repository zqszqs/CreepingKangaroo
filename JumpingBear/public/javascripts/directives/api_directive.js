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
    return function(scope, element, attrs) {
            // this gives us the native JS object
            var el = element[0];

            el.draggable = true;

            el.addEventListener(
                'dragstart',
                function(e) {
                    e.dataTransfer.effectAllowed = 'move';
                    e.dataTransfer.setData("type", attrs['type']);
                    e.dataTransfer.setData("value", attrs['value']);
                    this.classList.add('drag');
                    return false;
                },
                false
            );

            el.addEventListener(
                'dragend',
                function(e) {
                    this.classList.remove('drag');
                    return false;
                },
                false
            );
        }
});

bearAPI.directive('droppable', function() {
    return {
            scope: {
                drop: '=',
                index: '='
            },
            link: function(scope, element) {
                // again we need the native object
                var el = element[0];

                el.addEventListener(
                    'dragover',
                    function(e) {
                        e.dataTransfer.dropEffect = 'move';
                        // allows us to drop
                        if (e.preventDefault) e.preventDefault();
                        this.classList.add('over');
                        return false;
                    },
                    false
                );

                el.addEventListener(
                    'dragenter',
                    function(e) {
                        this.classList.add('over');
                        return false;
                    },
                    false
                );

                el.addEventListener(
                    'dragleave',
                    function(e) {
                        this.classList.remove('over');
                        return false;
                    },
                    false
                );

                el.addEventListener(
                    'drop',
                    function(e) {
                        var type = e.dataTransfer.getData("type");
                        var value = e.dataTransfer.getData("value");
                        // Stops some browsers from redirecting.
                        if (e.stopPropagation) e.stopPropagation();
                        this.classList.remove('over');
                        scope.drop(scope.index, type, value);
                        return false;
                    },
                    false
                );
            }
        }
});

bearAPI.directive('hfInputBody', function() {
    return {
        restrict: 'EA',
        scope: {
            bodyContent: '='
        },
        template: "<div><hf-input-object object-value='bodyContent'></hf-input-object></div>",
        link: function (scope, element, attrs) {
        }
    };
});

bearAPI.directive('hfInputObject', function($compile) {
    return {
        restrict: 'EA',
        scope: {
            objectValue: '='
        },
        link: function (scope, element, attrs) {
            scope.fields = [];
            scope.onDrop = function (index, arg0, arg1) {
                scope.fields[index].value.props[arg0] = arg1;
                scope.$apply();
            };
            for (n in scope.objectValue) {
                scope.fields.push({name: n, type: scope.objectValue[n].constructor, value: scope.objectValue[n]});
            }
            var template = "<div class='inline'>{</div><div class='indent'>" +
                "<div ng-repeat='f in fields'><div droppable drop='onDrop' index='$index'><div class='inline' style='vertical-align: top'>\"{{f.name}}\": </div>" +
                "<div ng-switch='f.type' class='inline'>" +
                "<hf-input-field ng-switch-when='LeafNode' field-value='f.value' ng-class='inline'></hf-input-field>" +
                "<hf-input-object ng-switch-when='Object' object-value='f.value'></hf-input-object>" +
                "<hf-input-array ng-switch-when='Array' array-value='f.value'></hf-input-array>" +
                "</div> " +
                "</div></div> </div><div class='inline'>}</div>";
            var $template = angular.element(template);
            $compile($template)(scope);
            element.append($template);
        }
    }
});

bearAPI.directive('hfInputArray', function($compile) {
return {
    restrict: 'EA',
    scope: {
        arrayValue: '='
    },
    link: function(scope, element, attrs) {
        scope.fields = scope.arrayValue.map(function (entry) {
            return {type: entry.constructor, value: entry}
        });
        var template = "<div class='inline'>[</div><div class='indent'><div ng-repeat='f in fields'>" +
            "<div ng-switch='f.type' class='inline'>" +
            "<hf-json-field ng-switch-when='LeafNode' field-value='f.value'></hf-json-field>" +
            "<hf-json-object ng-switch-when='Object' object-value='f.value'></hf-json-object>" +
            "<hf-json-array ng-switch-when='Array' array-value='f.value'></hf-json-array>" +
            "</div> " +
            "</div> </div><div class='inline'>]</div> ";
        var $template = angular.element(template);
        $compile($template)(scope);
        element.append($template);
    }
    }
});

bearAPI.directive('hfInputField', function() {
    return {
        restrict: 'EA',
        scope: {
            fieldValue: "="
        },
        template: '<div class="inline"><input ng-model="fieldValue.value"></input></div><div class="inline" ng-repeat="prop in fieldValue.props"><div class="inline body_prop body_error" ng-class="">{{prop}}</div></div>',
        compile: function (element, attrs) {
            return {
                pre: function (scope, element, attrs) {

                }
            }
        },
        link: function (scope, element, attrs) {
            console.log(scope.fieldValue)
        }

    }
});