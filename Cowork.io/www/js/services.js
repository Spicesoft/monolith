angular.module('starter')

.service('CalendarService', function($q) {
    var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();   

  return{
      eventSource: [[]],
      addEvent: function(event){
        console.log('Adding event to calendar');
        console.log(this.eventSource);
        this.eventSource.push([event]);
        console.log(this.eventSource);
      }
    }
})

.factory('ReservationsService', function($q) {

    var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();
    var h = date.getHours();

  return {
    reservations : [
      { title: 'Bureau',           start: new Date(y, m, d, h),        end: new Date(y, m, d, h + 1), id: 1 },
      { title: 'Poste de travail', start: new Date(y, m, d + 1, h),    end: new Date(y, m, d + 1, h + 1), id: 2 },
      { title: 'Bureau',           start: new Date(y, m, d + 2, h),    end: new Date(y, m, d + 2, h + 2), id: 3 },
      { title: 'Salle de réunion', start: new Date(y, m, d + 3, h),    end: new Date(y, m, d + 3, h + 3), id: 4 },
      { title: 'Poste de travail', start: new Date(y, m, d + 4, h),    end: new Date(y, m, d + 4, h + 4), id: 5 },
      { title: 'Bureau',            start: new Date(y, m, 1, h),       end: new Date(y, m, 1, h + 1), id: 6 }
    ],
    getReservations: function() {
      return this.reservations
    },
    getReservation: function(reservationsId) {
      var dfd = $q.defer()
      //console.log("GetResa");
      this.reservations.forEach(function(reservation) {
        //console.log("res.id = " + reservation.id + typeof reservation.id + "  resId = " + reservationsId + typeof reservationsId);
        if (reservation.id.toString() === reservationsId) {
          dfd.resolve(reservation)
        }
      })
      return dfd.promise
    },
    reservationsTitle: [
        { title: 'Bureau', id:1},
        { title: 'Salle de réunion', id:2},
        { title: 'Poste de travail', id:3},
        { title: 'Place de parking', id:4},
        { title: 'Place d\'héliport', id:5},
        { title: 'Court de Tennis', id:6},
        { title: 'Terrain de polo', id:7},
        { title: 'Placard à balais', id:8},
        { title: 'Rooftop', id:9},
        { title: 'Terrain de pétanque', id:10},
        { title: 'Stade de France', id:11},
        { title: 'Parc des Princes', id:12},
        { title: 'Région PACA', id:13},
        { title: '5, 4, 3, 0 et PAF pastèque', id:14},
        { title: 'Bassin olympique', id:15}
        ],
    generateReservation: function(id){
      d = Math.floor(Math.random() * 5);
      h = Math.floor(Math.random() * 9) + 6;
      t = Math.floor(Math.random() * 3) + 1;
      return reservation = {title: this.reservationsTitle[Math.floor(Math.random() * this.reservationsTitle.length)].title, start: new Date(y, m, d, h), end: new Date(y, m, d, h+t), id: id++};
    },
    addReservation: function(reservation){
        console.log('adding resa');
        this.reservations.push(reservation);
    }
  }
})

.service('AuthService', function($q, $http, USER_ROLES) {
  var LOCAL_TOKEN_KEY = 'TokenKey';
  var username = '';
  var isAuthenticated = false;
  var role = '';
  var authToken;

 
  function loadUserCredentials() {
    var token = window.localStorage.getItem(LOCAL_TOKEN_KEY);
    if (token) {
      useCredentials(token);
    }
  }
 
  function storeUserCredentials(token) {
    window.localStorage.setItem(LOCAL_TOKEN_KEY, token);
    useCredentials(token);
  }
 
  function useCredentials(token) {
    username = token.split('.')[0];
    isAuthenticated = true;
    authToken = token;
 
    if (username == 'admin') {
      role = USER_ROLES.admin
    }
    if (username == 'user') {
      role = USER_ROLES.public
    }
 
    // Set the token as header for your requests!
    //$http.defaults.headers.common['X-Auth-Token'] = token;
    $http.defaults.headers.common['Authorization'] = 'Token ' + token;
  }
 
  function destroyUserCredentials() {
    authToken = undefined;
    username = '';
    isAuthenticated = false;
    //$http.defaults.headers.common['X-Auth-Token'] = undefined;
    $http.defaults.headers.common['Authorization'] = undefined;
    window.localStorage.removeItem(LOCAL_TOKEN_KEY);
  }

  var getToken = function(){
    var token = window.localStorage.getItem(LOCAL_TOKEN_KEY);
    return token;
  };
 
  var login = function(name, pw) {
    return $q(function(resolve, reject) {
      if ((name == 'admin' && pw == '1') || (name == 'user' && pw == '1')) {
        // Make a request and receive your auth token from your server
        /**
          
          Sending username + password and receive Token from server

        **/
        storeUserCredentials(name + '.ServerToken');
        resolve('Login success.');
      } else {
        reject('Login Failed.');
      }
    });
  };
 
  var logout = function() {
    destroyUserCredentials();
  };
 
  var isAuthorized = function(authorizedRoles) {
    if (!angular.isArray(authorizedRoles)) {
      authorizedRoles = [authorizedRoles];
    }
    return (isAuthenticated && authorizedRoles.indexOf(role) !== -1);
  };
 
  loadUserCredentials();
 
  return {
    login: login,
    logout: logout,
    isAuthorized: isAuthorized,
    getToken: getToken,
    isAuthenticated: function() {return isAuthenticated;},
    username: function() {return username;},
    role: function() {return role;}
  };
})


/*
  Used to mock server response
*/
.factory('AuthInterceptor', function ($rootScope, $q, AUTH_EVENTS) {
  return {
    responseError: function (response) {
      $rootScope.$broadcast({
        401: AUTH_EVENTS.notAuthenticated,
        403: AUTH_EVENTS.notAuthorized
      }[response.status], response);
      return $q.reject(response);
    }
  };
})
 
.config(function ($httpProvider) {
  $httpProvider.interceptors.push('AuthInterceptor');
});