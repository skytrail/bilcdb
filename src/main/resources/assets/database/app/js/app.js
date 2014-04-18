'use strict';


// Declare app level module which depends on filters, and services
angular.module('myApp', [
  'ngRoute',
  'myApp.filters',
  'myApp.services',
  'myApp.directives',
  'myApp.controllers'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/view1', {templateUrl: 'app/partials/partial1.html', controller: 'MyCtrl1'});
  $routeProvider.when('/view2', {templateUrl: 'app/partials/partial2.html', controller: 'MyCtrl2'});
  $routeProvider.when('/login', {templateUrl: 'app/partials/login.html', controller: 'MyCtrl3'});
  $routeProvider.otherwise({redirectTo: '/view1'});
}]);
