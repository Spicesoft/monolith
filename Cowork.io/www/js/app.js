// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.controllers' is found in controllers.js
var app = angular.module('starter', ['ionic', 'ngMockE2E', 'ui.calendar', 'tc.chartjs', 'ionic-datepicker', 'ionic-timepicker']);

app
.config(function($compileProvider ){
  // Allowing to pen URI starting by geo: https: and file:
  // This used to 
  $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|file|geo):/);  //  /^\s*(https?|ftp|mailto|file|tel|geo):/
})

.run(function($ionicPlatform) {
  $ionicPlatform.ready(function() {
    // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
    // for form inputs)
    if (window.cordova && window.cordova.plugins.Keyboard) {
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
      cordova.plugins.Keyboard.disableScroll(true);
    }
    if (window.StatusBar) {
      // org.apache.cordova.statusbar required
      StatusBar.styleLightContent();
    }
  });
})


.config(['$ionicConfigProvider', function($ionicConfigProvider) {

    $ionicConfigProvider.tabs.position('bottom'); // other values: top

}])

.config(function($stateProvider, $urlRouterProvider) {
  $stateProvider

  .state('app', {
    url: '/app',
    abstract: true,
    templateUrl: 'templates/menu.html',
    controller: 'AppCtrl'
  })

  .state('login', {
    url: '/login',
        templateUrl: 'templates/login.html',
        controller: 'LoginCtrl'
  })

  .state('app.logout',{
    url: '/logout',
    controller: 'AppCtrl'
  })

  .state('app.profile', {
    url: '/profile',
    views: {
      'menuContent': {
        templateUrl: 'templates/profile.html',
        controller: 'ProfileCtrl'
      }
    }
  })

  .state('app.center', {
    url: '/center',
    views: {
      'menuContent': {
        templateUrl: 'templates/center.html'
      }
    }
  })

  .state('app.nfc', {
    url: '/nfc',
    views: {
      'menuContent': {
        templateUrl: 'templates/nfc.html',
        controller: 'NfcCtrl'
      }
    }
  })

  .state('app.newReservation', {
    url: '/newReservation',
    views: {
      'menuContent': {
        templateUrl: 'templates/new-reservation.html',
        controller: 'NewReservationCtrl'
      }
    }
  })

  .state('app.reservations', {
      url: '/reservations',
      resolve: {
        reservations: function(ReservationsService) {
        return ReservationsService.getReservations()
        }
      },
      views: {
        'menuContent': {
          templateUrl: 'templates/reservations.html',
          controller: 'ReservationsCtrl'
        }
      }
    })

  .state('app.single', {
    url: '/reservations/:reservationsId',
    resolve: {
      reservation: function($stateParams, ReservationsService) {
      return ReservationsService.getReservation($stateParams.reservationsId)
      }
    },
    views: {
      'menuContent': {
        templateUrl: 'templates/reservation.html',
        controller: 'ReservationCtrl'
      }
    }
  });
  // if none of the above states are matched, use this as the fallback
  $urlRouterProvider.otherwise(function ($injector, $location) {
    var $state = $injector.get("$state");
    $state.go("app.profile");
  });
})

.run(function($httpBackend){
  $httpBackend.whenGET('http://localhost:8100/valid')
        .respond({message: 'This is my valid response!'});
  $httpBackend.whenGET('http://localhost:8100/notauthenticated')
        .respond(401, {message: "Not Authenticated"});
  $httpBackend.whenGET('http://localhost:8100/notauthorized')
        .respond(403, {message: "Not Authorized"});
 
  $httpBackend.whenGET(/templates\/\w+.*/).passThrough();
 })

.run(function ($rootScope, $state, AuthService, AUTH_EVENTS) {
  $rootScope.$on('$stateChangeStart', function (event,next, nextParams, fromState) {
 
    if ('data' in next && 'authorizedRoles' in next.data) {
      var authorizedRoles = next.data.authorizedRoles;
      if (!AuthService.isAuthorized(authorizedRoles)) {
        event.preventDefault();
        $state.go($state.current, {}, {reload: true});
        $rootScope.$broadcast(AUTH_EVENTS.notAuthorized);
      }
    }
 
    if (!AuthService.isAuthenticated()) {
      if (next.name !== 'login') {
        event.preventDefault();
        $state.go('login');
      }
    }
  });
})


