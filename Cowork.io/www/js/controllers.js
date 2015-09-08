angular.module('starter')

.controller('AppCtrl', function($rootScope, $scope, $state, $ionicPopup, AuthService, AUTH_EVENTS) {

  // With the new view caching in Ionic, Controllers are only called
  // when they are recreated or on app start, instead of every page change.
  // To listen for when this page is active (for example, to refresh data),
  // listen for the $ionicView.enter event:
  //$scope.$on('$ionicView.enter', function(e) {
  //});

  // Hide NFC menu on non-Android devices
  if (ionic.Platform.isAndroid()){
    $scope.nfcMenu = true;
  }else {
    $scope.nfcMenu = false;
  }

  $scope.logout = function() {
    var confirmPopup = $ionicPopup.confirm({
     title: 'Logout',
     template: 'Are you shure you want to log out ?'
   });

   confirmPopup.then(function(res) {
     if(res) {
      AuthService.logout();
      $state.go('login', {}, {reload: true});
     } 
   });
  };

  $scope.username = AuthService.username();
 
  $scope.$on(AUTH_EVENTS.notAuthorized, function(event) {
    var alertPopup = $ionicPopup.alert({
      title: 'Unauthorized!',
      template: 'You are not allowed to access this resource.'
    });
  });
 
  $scope.$on(AUTH_EVENTS.notAuthenticated, function(event) {
    AuthService.logout();
    $state.go('app.login');
    var alertPopup = $ionicPopup.alert({
      title: 'Session Lost!',
      template: 'Sorry, You have to login again.'
    });
  });
 
  $rootScope.setCurrentUsername = function(name) {
    $rootScope.username = name;
  };
})

.controller('CalendarCtrl', function($scope, CalendarService, uiCalendarConfig){   

  $scope.eventSources = CalendarService.eventSource;

  $scope.uiConfig = {
  calendar:{
    defaultView: 'agendaDay',
    height: 'auto',
    editable: true,
    header:{
      right: 'today prev,next',
      left: 'month,agendaDay',
      center: 'title'
    },
    dayClick: $scope.alertEventOnClick,
    eventDrop: $scope.alertOnDrop,
    eventResize: $scope.alertOnResize
  }
};

})

.controller('ReservationsCtrl', function($scope, reservations, $ionicPopup, ReservationsService, CalendarService) {
  $scope.reservations = reservations;

  id = $scope.reservations.length;

  $scope.doDelete = function(item){

    var confirmPopup = $ionicPopup.confirm({
     title: 'Delete',
     template: 'Are you shure you want to delete this res?'
    });

    confirmPopup.then(function(res) {
     if(res) {
          console.log("Deleting");
    var index = $scope.reservations.indexOf(item);
    $scope.reservations.splice(index, 1); 
     } 
   });
  };

})

.controller('ReservationCtrl', function($scope, $stateParams, reservation) {
    $scope.reservationId = $stateParams.reservationsId;
    $scope.reservation = reservation;
})

.controller('LoginCtrl', function($rootScope, $scope, $state, $ionicPopup, AuthService) {
  //$scope.loginData = {};
  
  $scope.login = function(loginData) {
    AuthService.login(loginData.username, loginData.password).then(function(authenticated) {
      $state.go('app.profile', {}, {reload: true});
      $scope.loginData = {};

      $rootScope.setCurrentUsername(loginData.username);
    },
    function(err) {
      var alertPopup = $ionicPopup.alert({
        title: 'Login failed!',
        template: 'Please check your credentials!'
      });
    });
  };
})

.controller('ProfileCtrl', function($scope) {
  /*
    Data that will be displayed in the accordion list
  */

  $scope.groups = [
  { title: 'Crédits', id: 1},
  { title: 'Informations', id: 2}
  ];

  $scope.groups[0] = {
      name: 'Crédits',
      icon: 'ion-social-euro',
      items: []
    };

  $scope.groups[1] = {
      name: 'Informations',
      icon: 'ion-information-circled',
      items: []
    };

  $scope.groups[0].items.push("Crédits d'impression");
  $scope.groups[0].items.push("Crédits de réservation");

  $scope.groups[1].items.push("Nom");
  $scope.groups[1].items.push("Prénom");

  
  /*
   * if given group is the selected group, deselect it
   * else, select the given group
   */
  $scope.toggleGroup = function(group) {
    if ($scope.isGroupShown(group)) {
      $scope.shownGroup = null;
    } else {
      $scope.shownGroup = group;
    }
    //console.log(group);
  };

  $scope.isGroupShown = function(group) {
    return $scope.shownGroup === group;
  };

  $scope.getIcon = function(group) {
    return group.icon;
  };
  
})

.controller('MapController', function($scope, $ionicLoading, $compile) {
  /*
    Initialize the map view at a specific LatLng, and add a marker on the map.
  */
  $scope.initialize = function() {
    var myLatlng = new google.maps.LatLng(48.871610, 2.347849);
    
    var mapOptions = {
      center: myLatlng,
      zoom: 16,
      mapTypeId: google.maps.MapTypeId.ROADMAP,
      disableDefaultUI: true
    };
    var map = new google.maps.Map(document.getElementById("map"),
        mapOptions);


    var marker = new google.maps.Marker({
      position: myLatlng,
      map: map,
      title: 'Mon centre'
    });

    google.maps.event.addListener(marker, 'click', function() {
      infowindow.open(map,marker);
    });

    $scope.map = map;

  }

  /*
    navUrl is open when the "Navigate" button is clicked.
    Each URI opens the Navigation app of the corresponding device (Android: Google maps app, IOS: apple maps app, other: google mpas web).
  */
  if (ionic.Platform.isAndroid()){
    $scope.navUrl = "geo:48.871656, 2.347848?q=9+rue+du+faubourg+poissonniere";
  }else if (ionic.Platform.isIOS()){
    $scope.navUrl = "http://maps.apple.com/?daddr=9+rue+du+faubourg+poissonniere";
  }else {
    $scope.navUrl = "https://www.google.fr/maps/place/48°52'18.0\"N+2°20'52.2\"E/@48.871656,2.347848,19z"; // 9 rue du faubourg poissonniere
  }

})

.controller('TenantsCtrl', function($scope, $http) {
  
        /*
        //$http.defaults.headers.common.Authorization = 'Token 961f17d3b6625cc6514209fdb2c6d6e333824715' ;
        //$http.get("https://backoffice.lite-staging.tandoori.pro/api/v1/tenants/?format=json")
        $http.get("http://jsonplaceholder.typicode.com/users/")
        .success(function(response) {$scope.tenants = response.records;
        console.log(response.records);
        });

        console.log(JSON.stringify($http.get("http://jsonplaceholder.typicode.com/users/")));
        */

        /*
        $.ajaxSetup({
            headers : {
              'Authorization' : 'Token 961f17d3b6625cc6514209fdb2c6d6e333824715',
            }
          });

        $.getJSON( "https://backoffice.lite-staging.tandoori.pro/api/v1/tenants/?format=json", function( data ) {
          var items = [];
          $.each( data, function( key, val ) {
            items.push( "<li id='" + key + "'>" + val + "</li>" );
            console.log(JSON.stringify(data));
          });
         
          $( "<ul/>", {
            "class": "my-new-list",
            html: items.join( "" )
          }).appendTo( "body" );
        });
        */
        
    })


.controller('PieChartCtrl', function( $scope ) {

    // Chart.js Data
    $scope.data = [
      {
        value: 300,
        color:'#F7464A',
        highlight: '#FF5A5E',
        label: 'Red'
      },
      {
        value: 50,
        color: '#46BFBD',
        highlight: '#5AD3D1',
        label: 'Green'
      },
      {
        value: 100,
        color: '#FDB45C',
        highlight: '#FFC870',
        label: 'Yellow'
      }
    ];

    // Chart.js Options
    $scope.options =  {

      // Sets the chart to be responsive
      responsive: true,

      //Boolean - Whether we should show a stroke on each segment
      segmentShowStroke : true,

      //String - The colour of each segment stroke
      segmentStrokeColor : '#fff',

      //Number - The width of each segment stroke
      segmentStrokeWidth : 2,

      //Number - The percentage of the chart that we cut out of the middle
      percentageInnerCutout : 0, // This is 0 for Pie charts

      //Number - Amount of animation steps
      animationSteps : 100,

      //String - Animation easing effect
      animationEasing : 'easeOutBounce',

      //Boolean - Whether we animate the rotation of the Doughnut
      animateRotate : true,

      //Boolean - Whether we animate scaling the Doughnut from the centre
      animateScale : false,

      //String - A legend template
      legendTemplate : '<ul class="tc-chart-js-legend"><% for (var i=0; i<segments.length; i++){%><li><span style="background-color:<%=segments[i].fillColor%>"></span><%if(segments[i].label){%><%=segments[i].label%><%}%></li><%}%></ul>'

    };

  })

.controller('BarChartCtrl', function( $scope ) {

    // Chart.js Data
    $scope.data = {
      labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
      datasets: [
        {
          label: 'dataset',
          fillColor: 'rgba(220,220,220,0.2)',
          strokeColor: 'rgba(220,220,220,1)',
          pointColor: 'rgba(220,220,220,1)',
          pointStrokeColor: '#fff',
          pointHighlightFill: '#fff',
          pointHighlightStroke: 'rgba(220,220,220,1)',
          data: [65, 59, 80, 81, 56, 55, 40]
        },
        {
          label: 'dataset',
          fillColor: 'rgba(151,187,205,0.2)',
          strokeColor: 'rgba(151,187,205,1)',
          pointColor: 'rgba(151,187,205,1)',
          pointStrokeColor: '#fff',
          pointHighlightFill: '#fff',
          pointHighlightStroke: 'rgba(151,187,205,1)',
          data: [28, 48, 40, 19, 86, 27, 90]
        }
      ]
    };

    // Chart.js Options
    $scope.options =  {

      // Sets the chart to be responsive
      responsive: true,

      ///Boolean - Whether grid lines are shown across the chart
      scaleShowGridLines : true,

      //String - Colour of the grid lines
      scaleGridLineColor : "rgba(0,0,0,.05)",

      //Number - Width of the grid lines
      scaleGridLineWidth : 1,

      //Boolean - Whether the line is curved between points
      bezierCurve : true,

      //Number - Tension of the bezier curve between points
      bezierCurveTension : 0.4,

      //Boolean - Whether to show a dot for each point
      pointDot : true,

      //Number - Radius of each point dot in pixels
      pointDotRadius : 4,

      //Number - Pixel width of point dot stroke
      pointDotStrokeWidth : 1,

      //Number - amount extra to add to the radius to cater for hit detection outside the drawn point
      pointHitDetectionRadius : 20,

      //Boolean - Whether to show a stroke for datasets
      datasetStroke : true,

      //Number - Pixel width of dataset stroke
      datasetStrokeWidth : 2,

      //Boolean - Whether to fill the dataset with a colour
      datasetFill : true,

      // Function - on animation progress
      onAnimationProgress: function(){},

      // Function - on animation complete
      onAnimationComplete: function(){},

      //String - A legend template
      legendTemplate : '<ul class="tc-chart-js-legend"><% for (var i=0; i<datasets.length; i++){%><li><span style="background-color:<%=datasets[i].strokeColor%>"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>'
    };

  })

.controller('RefreshCtrl', function($scope, $timeout, ReservationsService, CalendarService) {

  id = $scope.reservations.length;

  $scope.doRefresh = function() {    
    console.log('Refreshing!');

    var date = new Date();  
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();
    var h = date.getHours();

    console.log('ID on refresh = ' + id);

    $timeout( function() {
      //simulate async response
      var res = ReservationsService.generateReservation(++id);
      ReservationsService.addReservation(res);
      CalendarService.addEvent(res);
      //$scope.reservations.push( {title: reservationsTitle[Math.floor(Math.random() * reservationsTitle.length)].title, start: new Date(y, m, d, h), end: new Date(y, m, d, h+1), id: id++} );

      //Stop the ion-refresher from spinning
      $scope.$broadcast('scroll.refreshComplete');
    
    }, 1000);  
  };
})

.controller('NfcCtrl', function($scope, AuthService){

  $scope.setMessageWithToken = function(){
    console.log('setNdefPushMessage: ' + AuthService.getToken());
    var message = [
      ndef.textRecord("AuthService.getToken()")
    ];
    nfc.share(message, function(){console.log("success")}, function(){console.log("failure")});
  };

  $scope.disableNfc = function(){
    //nfcHce.disableForegroundNdefPush();
    nfc.unshare();
    //nfcHce.setNdefPushMessage(null, null, null, null);
  };

})

.controller('NewReservationCtrl', function($scope, $state, $ionicHistory, $ionicPopup, ReservationsService, CalendarService){

      $scope.reservations = ReservationsService.reservationsTitle;
      id = ReservationsService.reservations.length;

      $scope.data = {
        availableOptions: ReservationsService.reservationsTitle,
        selectedOption: ReservationsService.reservationsTitle[0]
      };


      var date = new Date();
      var startTime = new Date(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours() + 1);
      var endTime = new Date(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours() + 2);

      var defaultStartTime = startTime.getHours() * 60 * 60;
      var defaultEndTime = endTime.getHours() * 60 * 60;


      $scope.startDatepickerObject = {
        titleLabel: 'Date réservation',  //Optional
        todayLabel: 'Today',  //Optional
        closeLabel: 'Close',  //Optional
        setLabel: 'Set',  //Optional
        setButtonType : 'button-assertive',  //Optional
        todayButtonType : 'button-assertive',  //Optional
        closeButtonType : 'button-assertive',  //Optional
        inputDate: new Date(),    //Optional
        mondayFirst: true,    //Optional
        //disabledDates: disabledDates, //Optional
        //weekDaysList: weekDaysList,   //Optional
        //monthList: monthList, //Optional
        templateType: 'popup', //Optional
        modalHeaderColor: 'bar-positive', //Optional
        modalFooterColor: 'bar-positive', //Optional
        from: new Date(2012, 8, 2),   //Optional
        to: new Date(2018, 8, 25),    //Optional
        callback: function (val) {    //Mandatory
            if (typeof(val) === 'undefined') {
              console.log('No date selected');
            } else {
              console.log('Selected date is : ', val)
              date = new Date(val);
            }
        }
      };

      $scope.startTimePicker = {
        inputEpochTime: defaultStartTime,
        callback: function (val) {
          if (typeof (val) === 'undefined') {
            console.log('Time not selected');
          } else {
            var selectedTime = new Date(val * 1000);
            console.log('Selected epoch is : ', val, 'and the time is ', selectedTime.getUTCHours(), ':', selectedTime.getUTCMinutes(), 'in UTC');
            startTime = selectedTime;
          }
        }
      };

      $scope.endTimePicker = {
        inputEpochTime: defaultEndTime,
        callback: function (val) {
          if (typeof (val) === 'undefined') {
            console.log('Time not selected');
          } else {
            var selectedTime = new Date(val * 1000);
            console.log('Selected epoch is : ', val, 'and the time is ', selectedTime.getUTCHours(), ':', selectedTime.getUTCMinutes(), 'in UTC');
            endTime = selectedTime;
          }
        }
      };

      $scope.cancelNewReservation = function(){
        $ionicHistory.nextViewOptions({
            disableBack: true
          });
        $state.go('app.reservations', {}, {reload: true});
        console.log('Cancel');
      };


      $scope.addNewReservation = function(){
        console.log('Resa title: ' + $scope.data.selectedOption.title + ' from : ' + date + startTime.getUTCHours() + ' to : ' + endTime.getUTCHours());

        if (new Date(date.getFullYear(), date.getMonth(), date.getDate(), startTime.getHours()) > new Date()){
          if (endTime > startTime)
          {
            ReservationsService.addReservation( {title: $scope.data.selectedOption.title, start: new Date(date.getFullYear(),
                                                                                                             date.getMonth(), 
                                                                                                             date.getDate(), 
                                                                                                             startTime.getHours(), 
                                                                                                             startTime.getMinutes()),
                      
                                                                                           end: new Date(date.getFullYear(),
                                                                                                             date.getMonth(), 
                                                                                                             date.getDate(), 
                                                                                                             endTime.getHours(), 
                                                                                                             endTime.getMinutes()),
                                                                                            id:  ++id });
            CalendarService.addEvent({title: $scope.data.selectedOption.title ,start: new Date(date.getFullYear(),
                                                                                               date.getMonth(), 
                                                                                               date.getDate(), 
                                                                                               startTime.getHours(), 
                                                                                               startTime.getMinutes()),
                      
                                                                                 end: new Date(date.getFullYear(),
                                                                                               date.getMonth(), 
                                                                                               date.getDate(), 
                                                                                               endTime.getHours(), 
                                                                                               endTime.getMinutes())});

        $ionicHistory.nextViewOptions({
            disableBack: true
          });
        $state.go('app.reservations', {}, {reload: true});
        console.log('Valid');
      }else{
        var alertPopup = $ionicPopup.alert({
           title: 'Time error',
           template: 'Time doesn\'t go backward here'
         });

         alertPopup.then(function(res) {
          console.log('OK');
         });
      }
    }else{
        var alertPopup = $ionicPopup.alert({
         title: 'Date error',
         template: 'Be kind don\'t book in the past'
       });

       alertPopup.then(function(res) {
        console.log('OK');
       });
    }

    };

});