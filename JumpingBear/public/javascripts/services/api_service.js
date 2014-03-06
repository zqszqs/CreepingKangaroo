/**
 * User: Hongfei Zhou
 * Date: 13-12-26
 * JS Description:
 */

bearAPI.factory('API', ['$http', function ($http) {
    var baseUrl = '/data/service/api';
    return  {
        get: function (apiId) {
            return $http.get(baseUrl + '/' + apiId);
        },
        save: function (api) {
            return $http.post(baseUrl + '/', api)
        },
        query: function () {
            return $http.get(baseUrl + '/');
        },
        queryBriefMap: function() {
            return $http.get(baseUrl + "/", {params: {action: 'brief_map'}})
        },
        queryBrief: function() {
            return $http.get(baseUrl + "/", {params: {action: 'brief'}})
        },
        del: function (apiId) {
            return $http.delete(baseUrl + '/' + apiId);
        },
        update: function (api) {
            return $http.put(baseUrl + '/' + api.id, api);
        }
    }
}]);


bearAPI.factory('SUITE', ['$http', function ($http) {
    var baseUrl = '/data/service/suite';
    return  {
        get: function (suiteId) {
            return $http.get(baseUrl + '/' + suiteId);
        },
        save: function (suite) {
            return $http.post(baseUrl + '/', suite)
        },
        query: function () {
            return $http.get(baseUrl + '/');
        },
        del: function (suiteId) {
            return $http.delete(baseUrl + '/' + suiteId);
        },
        update: function (suite) {
            return $http.put(baseUrl + '/' + suite.id, suite);
        }
    }
}]);

bearAPI.factory('TEST', ['$http', function ($http) {
    var baseUrl = '/data/service/test';
    return  {
        get: function (testId) {
            return $http.get(baseUrl + '/' + testId);
        },
        save: function (test) {
            return $http.post(baseUrl + '/', test)
        },
        query: function () {
            return $http.get(baseUrl + '/');
        },
        del: function (testId) {
            return $http.delete(baseUrl + '/' + testId);
        },
        update: function (test) {
            return $http.put(baseUrl + '/' + test.id, test);
        },
        getBySuite: function (suiteId) {
            return $http.get(baseUrl + '/suite/' + suiteId);
        }
    }
}]);


bearAPI.factory('REQUEST', ['$http', function ($http) {
    var baseUrl = '/data/service/request';
    return  {
        get: function (requestId) {
            return $http.get(baseUrl + '/' + requestId);
        },
        save: function (request) {
            return $http.post(baseUrl + '/', request)
        },
        query: function () {
            return $http.get(baseUrl + '/');
        },
        del: function (requestId) {
            return $http.delete(baseUrl + '/' + requestId);
        },
        update: function (request) {
            return $http.put(baseUrl + '/' + request.id, request);
        },
        getByTest: function (testId) {
            return $http.get(baseUrl + '/test/' + testId);
        }
    }
}]);

bearAPI.factory('EXECUTOR', ['$http', function ($http) {
    var baseUrl = '/function/service/execute/';
    return  {
        executeRequest: function (requestId) {
            return $http.put(baseUrl + 'request/' + requestId)
        },
        executeTest: function (testId) {
            return $http.put(baseUrl + 'test/' + testId)
        },
        executeSuite: function (suiteId) {
            return $http.put(baseUrl + 'suite/' + suiteId)
        }
    }
}]);

bearAPI.factory('STATUS', ['$location', function ($location) {
    var socket;

    var sendMessage = function (msg) {
        if (socket.readyState == 0)
            setTimeout(function () { sendMessage(msg);}, 500);
        else
            socket.send(msg);
    };

    return {
        use: function (messageProcessor) {
            if (typeof socket == 'undefined') {
                socket = new WebSocket("ws://" + $location.host() + ":" + $location.port() + "/function/service/execute/status");
            }
            socket.onmessage = messageProcessor;
        },
        close: function() {
            socket.close()
        },
        ofTest: function (id) {
            sendMessage(JSON.stringify({
                forItem: "TEST",
                testId: id
            }))
        },
        ofRequest: function(tid, rid) {
            sendMessage(JSON.stringify({
                forItem: "REQUEST",
                testId: tid,
                requestId: rid
            }))
        },
        ofAllRequests: function (tid) {
            sendMessage(JSON.stringify({
                forItem: "ALL_REQUESTS",
                testId: tid
            }))
        }
    }
}]);