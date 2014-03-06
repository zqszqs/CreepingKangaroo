/**
 * User: Hongfei Zhou
 * Date: 14-1-6
 * JS Description:
 */

var isEmpty = function (node) {
    return (typeof node == 'undefined') || (node.length == 0);
};

var removeEmpty = function (headers) {
    if (headers.length == 0) return;
    var lastHead = headers[headers.length - 1];
    if (isEmpty(lastHead.name) && isEmpty(lastHead.value)) {
        headers.pop();
        removeEmpty(headers);
    }
};

var methodColor = {
    GET: '#B3FFB5',
    POST: '#B29143',
    PUT: '#68ABCC',
    DELETE: '#FF9BA6',
    HEAD: '#B2B055',
    OPTIONS: '#30B283',
    PATCH: '#C5EBFF',
    COPY: '#FFFDAC',
    LINK: '#CC757A',
    UNLINK: '#FF9D13',
    PURGE: '#326DB2'
};

var httpMethod = [
    [
        {name: 'GET', color: '#B3FFB5'},
        {name: 'POST', color: '#B29143'},
        {name: 'PUT', color: '#68ABCC'}
    ],
    [
        {name: 'DELETE', color: '#FF9BA6'},
        {name: 'HEAD', color: '#B2B055'},
        {name: 'OPTIONS', color: '#30B283'}
    ],
    [
        {name: 'PATCH', color: '#C5EBFF'},
        {name: 'COPY', color: '#FFFDAC'},
        {name: 'LINK', color: '#CC757A'}
    ],
    [
        {name: 'UNLINK', color: '#FF9D13'},
        {name: 'PURGE', color: '#326DB2'}
    ]
];

bearAPI.controller("RequestController", ['$scope', 'REQUEST', 'EXECUTOR', 'STATUS', function ($scope, REQUEST, EXECUTOR, STATUS) {
    $scope.reqView = {
        httpMethods: httpMethod,
        currentHover: true,
        gapHeight: '90px',
        openOperate: -1,
        startAddRequest: false,
        selectedMethod: httpMethod[0][0],
        startSelectMethod: false,
        methodColor: methodColor,
        executeAllRequestImg: 'execute.png'
    };
    $scope.buf = {};

    var updateRequestView = function (requestMeta, status) {
        var innerUpdateFunction;
        if (typeof status == 'undefined')
            return;
        switch (status.status) {
            case "SUCCESS":
            case "FAILURE":
            case "SKIPPED":
                innerUpdateFunction = function () {
                    requestMeta.hasResult = true;
                    requestMeta.result = {input: status.input, output: status.output};
                    requestMeta.executePng = "execute.png";
                };
                break;
            case "NONE":
                innerUpdateFunction = function () {
                    requestMeta.hasResult = false;
                    requestMeta.result = {};
                    requestMeta.executePng = "execute.png";
                };
                break;
            case "EXECUTING":
                innerUpdateFunction = function () {
                    requestMeta.executePng = "executing.gif";
                    requestMeta.hasResult = false;
                    requestMeta.result = {};
                    $scope.reqView.executeAllRequestImg = "executing.gif";
                };
                break;
            case "PENDING":
                innerUpdateFunction = function () {
                    requestMeta.executePng = "waiting.gif";
                    requestMeta.hasResult = false;
                    requestMeta.result = {};
                    $scope.reqView.executeAllRequestImg = "executing.gif";
                };
                break;
        }
        $scope.$apply(innerUpdateFunction);
    };

    var requestsViewUpdate = function (requests, requestMetas, statuses) {
        var statusMap = {};
        if (typeof statuses != 'undefined') {
            statuses.map(function (s) {
                statusMap[s.id] = s
            });
        }
        for (var i = 0; i < requests.length; ++i) {
            var result = statusMap[requests[i].id];
            if (typeof result != 'undefined') {
                requestMetas[i].hasResult = true;
                requestMetas[i].result = result;
                updateRequestView(requestMetas[i], result);
            } else {
                requestMetas[i].hasResult = false;
                requestMetas[i].result = {};
                updateRequestView(requestMetas[i], {status: "NONE"});
            }
        }
    };

    var requestStateUpdater = function (msg) {
        var statusObj = JSON.parse(msg.data);
        for (var i = 0; i < $scope.requests.length; ++i) {
            if ($scope.requests[i].id == statusObj.requestId) {
                updateRequestView($scope.requestMetas[i], statusObj);
            }
        }
        if (statusObj.status == 'EXECUTING' || statusObj.status == 'PENDING')
            setTimeout(function () {
                STATUS.use(requestStateUpdater);
                STATUS.ofRequest(statusObj.testId, statusObj.requestId);
            }, 1000);
        else
            $scope.reqView.executeAllRequestImg = "execute.png";
        $scope.$apply();
    };

    var allRequestStateUpdater = function (msg) {
        var statusObj = JSON.parse(msg.data);
        if (statusObj.status == 'EXECUTING' || statusObj.status == 'PENDING') {
            requestsViewUpdate($scope.requests, $scope.requestMetas, statusObj.requests);
            $scope.reqView.executeAllRequestImg = 'executing.gif';
            setTimeout(function () {
                STATUS.use(allRequestStateUpdater);
                STATUS.ofAllRequests(statusObj.testId);
            }, 1000);
        }
        else if (statusObj.status == 'NONE') {
            $scope.reqView.executeAllRequestImg = 'execute.png';
            requestsViewUpdate($scope.requests, $scope.requestMetas);
        }
        else {
            requestsViewUpdate($scope.requests, $scope.requestMetas, statusObj.requests);
            $scope.reqView.executeAllRequestImg = 'execute.png';
        }
        $scope.$apply()
    };

    REQUEST.getByTest($scope.testToRequest.currentTest.id).success(function (data) {
        $scope.requestMetas = Array.apply(null, new Array(data.length)).map(function (a) {
            return {executePng: 'execute.png'};
        });
        $scope.requests = data;
        setTimeout(new function () {
            STATUS.use(allRequestStateUpdater);
            STATUS.ofAllRequests($scope.testToRequest.currentTest.id);
        }, 1000);
    });
    $scope.$watch('testToRequest.currentTest', function () {
        $scope.requests = [];
        REQUEST.getByTest($scope.testToRequest.currentTest.id).success(function (data) {
            $scope.requests = data;
            $scope.requestMetas = Array.apply(null, new Array($scope.requests.length)).map(function (a) {
                return {};
            });
        });
    });

    $scope.getImage = function (index) {
        if ($scope.requestMetas[index].executePng)
            return $scope.requestMetas[index].executePng;
        else
            return "execute.png";
    };

    $scope.editRequest = function (index) {
        $scope.closeSelectMethod();
        $scope.reqView.startAddRequest = false;
        if ($scope.reqView.openOperate == index && $scope.reqView.gapContent == 'editRequest') {
            $scope.reqView.openOperate = -1;
        } else {
            $scope.newRequest = $scope.requests[index];
            $scope.newRequest.index = index;
            var headerNum = 0;
            if (typeof $scope.newRequest.input.headers != 'undefined')
                headerNum = $scope.newRequest.input.headers.length;
            if (typeof $scope.newRequest.output.headers != 'undefined')
                headerNum = $scope.newRequest.output.headers.length + headerNum;
            var height = 855 + headerNum * 34;
            $scope.reqView.gapHeight = height + 'px';
            $scope.reqView.gapContent = 'editRequest';
            $scope.reqView.openOperate = index;
            $scope.button = {
                submit: function () {
                    REQUEST.update($scope.newRequest).success(function (data) {
                        $scope.requestMetas[index].requestModified = true;
                        $scope.reqView.openOperate = -1;
                    })
                },
                discard: function () {
                    $scope.closeSelectMethod();
                    $scope.reqView.openOperate = -1;
                }
            };
        }
    };

    $scope.deleteRequest = function (index) {
        $scope.reqView.startAddRequest = false;
        if ($scope.reqView.openOperate == index && $scope.reqView.gapContent == 'deleteRequest') {
            $scope.reqView.openOperate = -1;
        } else {
            $scope.newRequest = $scope.requests[index];
            $scope.reqView.gapHeight = '110px';
            $scope.reqView.gapContent = 'deleteRequest';
            $scope.reqView.openOperate = index;
            $scope.button = {
                submit: function () {
                    REQUEST.del($scope.newRequest.id).success(function (data) {
                        $scope.requests.splice(index, 1);
                        $scope.requestMetas.splice(index, 1);
                        $scope.reqView.openOperate = -1;
                    })
                },
                discard: function () {
                    $scope.reqView.openOperate = -1;
                }
            };
        }
    };

    var swap = function (array, a, b) {
        if (b == 0 || b >= array.length) return;
        array[a] = array.splice(b, 1, array[a])[0];
        var tem = array[a].position;
        array[a].position = array[b].position;
        array[b].position = tem;
    };

    $scope.moveUp = function (index) {
        $scope.reqView.openOperate = -1;
        swap($scope.requests, index - 1, index);
        swap($scope.requestMetas, index - 1, index);
        REQUEST.update($scope.requests[index - 1]);
        REQUEST.update($scope.requests[index]);
    };

    $scope.moveDown = function (index) {
        $scope.moveUp(index + 1);
    };

    var getIndex = function () {
        if ($scope.requests.length == 0) return 0;
        else return $scope.requests[$scope.requests.length - 1].position + 1
    };

    $scope.addNewRequest = function () {
        $scope.newRequest = {
            testId: $scope.testToRequest.currentTest.id,
            position: getIndex(),
            input: {
                method: 'GET',
                headers: []
            },
            output: {
                headers: []
            }
        };
        $scope.reqView.startAddRequest = true;
        $scope.button = {
            submit: function () {
                removeEmpty($scope.newRequest.input.headers);
                if ($scope.newRequest.input.headers.length == 0)
                    delete $scope.newRequest.input['headers'];
                removeEmpty($scope.newRequest.output.headers);
                if ($scope.newRequest.output.headers.length == 0)
                    delete $scope.newRequest.output['headers'];
                REQUEST.save($scope.newRequest).success(function (data) {
                    $scope.newRequest.id = data.id;
                    $scope.requests.push($scope.newRequest);
                    $scope.requestMetas.push({requestModified: true});
                    $scope.reqView.startAddRequest = false;
                })
            },
            discard: function () {
                $scope.reqView.startAddRequest = false;
            }
        };
        $scope.$watch('newRequest.input.headers', function () {
            removeEmpty($scope.newRequest.input.headers);
            if (typeof $scope.newRequest.input.headers != 'undefined')
                $scope.newRequest.input.headers.push({});
        }, true);

        $scope.$watch('newRequest.output.headers', function () {
            removeEmpty($scope.newRequest.output.headers);
            if (typeof $scope.newRequest.output.headers != 'undefined')
                $scope.newRequest.output.headers.push({});
        }, true);
    };

    $scope.selectMethod = function (method) {
        $scope.newRequest.input.method = method.name;
        $scope.reqView.selectedMethod = method;
        $scope.reqView.startSelectMethod = false;
    };

    $scope.selectMethodPop = function () {
        $scope.reqView.startSelectMethod = !$scope.reqView.startSelectMethod;
    };

    $scope.closeSelectMethod = function () {
        $scope.reqView.startSelectMethod = false;
    };

    $scope.executeRequest = function (index) {
        var request = $scope.requests[index];
        EXECUTOR.executeRequest(request.id);
        $scope.requestMetas[index].executePng = 'waiting.gif';
        setTimeout(function () {
            STATUS.use(requestStateUpdater);
            STATUS.ofRequest($scope.testToRequest.currentTest.id, request.id);
        }, 1000);
    };

    $scope.executeAllRequests = function () {
        var test = $scope.testToRequest.currentTest;
        EXECUTOR.executeTest(test.id);
        $scope.reqView.executeAllRequestImg = 'waiting.gif';
        setTimeout(function () {
            STATUS.use(allRequestStateUpdater);
            STATUS.ofAllRequests(test.id);
        }, 1000);
    };

    $scope.showDetail = function (index) {
        if ($scope.reqView.openOperate == index && $scope.reqView.gapContent == 'showResult') {
            $scope.reqView.openOperate = -1;
        } else {
            $scope.currentResult = $scope.requestMetas[index].result;
            $scope.reqView.gapHeight = '5000px';
            $scope.reqView.gapContent = 'showResult';
            $scope.reqView.openOperate = index;
        }
    };

    $scope.hasResult = function (request) {
        return $scope.results.filter(function (o) {
            return o.id == request.id;
        });
    };

}])
;
