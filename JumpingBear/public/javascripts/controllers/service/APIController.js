/**
 * User: Hongfei Zhou
 * Date: 14-1-6
 * JS Description:
 */


var domains = [
    ['Account', 'Account Management', 'Ad Commerce', 'Analytics'],
    ['Catalog', 'Common', 'Content', 'CS'],
    ['Device', 'Discount'],
    ['Fulfillment'],
    ['i18N', 'Infrastructure', 'International', 'Inventory'],
    ['Mobile'],
    ['Partner Integration', 'Payment', 'Pricing', 'Purchase'],
    ['Recommendations'],
    ['Search', 'Security', 'Sell', 'Stub Pro'],
    ['Tracking'],
    ['User']
];

var inValid = function (src) {
    return !((typeof src) != 'undefined' && src.length > 1);
};

bearAPI.controller('ListAPIController', ['$scope', 'API', function ($scope, API) {
    $scope.currentTitle = 'All APIs';
    $scope.newAPI = {
        domain: 'Account'
    };

    $scope.selectView = function (view) {
        $scope.viewSelect = view;
    };

    $scope.prependAPI = function (api) {
        $scope.apis.unshift(api);
    };

    $scope.viewSelect = 'loading';

    API.query().success(function (apis) {
        $scope.viewSelect = 'apilist';
        $scope.apiMetas = Array.apply(null, new Array(apis.length)).map(function (a) {
            return {expandHeight: '0'};
        });
        $scope.apis = apis;
    });

    $scope.updateHover = function (index, isHover) {
        if (isHover)
            $scope.currentHover = index;
        else
            $scope.currentHover = -1;
    };

    $scope.updateAPIName = function (api) {
        API.update(api);
    };

    $scope.editAPI = function (index) {
        if ($scope.editIndex == index && $scope.singleApiOps == 'editAPI') {
            $scope.editIndex = -1;
            $scope.updateExpandHeight(0);
        }
        else {
            $scope.editIndex = index;
            $scope.singleApiOps = 'editAPI';
            $scope.updateExpandHeight('230px');
        }
    };

    $scope.deleteAPI = function (index) {
        if ($scope.editIndex == index && $scope.singleApiOps == 'deleteAPI') {
            $scope.editIndex = -1;
            $scope.updateExpandHeight(0);
        }
        else {
            $scope.editIndex = index;
            $scope.singleApiOps = 'deleteAPI';
            $scope.updateExpandHeight('120px');
        }
    };

    $scope.discardDeleteAPI = function (index) {
        $scope.editIndex = -1;
    };

    $scope.submitDeleteAPI = function (index) {
        API.del($scope.apis[index].id).success(function () {
            $scope.editIndex = -1;
            $scope.apis.splice(index, 1);
        });
    };

    $scope.updateExpandHeight = function (height) {
        $scope.currentHeight = height;
    };
}]);


bearAPI.controller('CreateAPIController', ['$scope', 'API', function ($scope, API) {
    $scope.allDomain = domains;
    $scope.submitBtnDisabled = true;

    $scope.newAPI = $scope.apis[$scope.$index];

    if (typeof $scope.newAPI == 'undefined')
        $scope.newAPI = {
            domain: 'Account'
        };

    $scope.toggleSelector = function () {
        $scope.showSelector = !$scope.showSelector;
        if ($scope.showSelector) {
            $scope.updateExpandHeight('720px');
        } else {
            $scope.updateExpandHeight('265px');
        }
    };

    $scope.$watch('newAPI.name + newAPI.endpoint + newAPI.domain', function () {
        $scope.submitBtnDisabled = inValid($scope.newAPI.name) ||
            inValid($scope.newAPI.endpoint) ||
            $scope.newAPI.endpoint.$valid;
    });

    $scope.discardAPI = function (api) {
        if (typeof api.id != 'undefined') {
            if ($scope.showSelector)
                $scope.toggleSelector();
            $scope.editAPI(-1);
        } else {
            $scope.newAPI = {
                domain: 'Account'
            };
            $scope.selectView("apilist");
        }
    };

    $scope.submitAPI = function (api) {
        if (typeof api.id == 'undefined') {
            API.save(api).success(function (data) {
                api.newCreateColor = true;
                api.id = data.id;
                $scope.prependAPI(api);
                $scope.selectView("apilist");
            });
        } else {
            API.update(api).success(function () {
                $scope.editAPI(-1);
                api.newCreateColor = true;
            });
        }
    };
}]);