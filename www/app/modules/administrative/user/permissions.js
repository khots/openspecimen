
angular.module('os.administrative.user.permissions', ['os.administrative.models', 'os.biospecimen.models'])
  .controller('UserPermissionsCtrl', function(
    $scope, $translate,
    user, permissions,
    PvManager, CollectionProtocol, Role, User) {
    var all = $translate.instant('user.all');
    var count = 1;  

    function init() {
      $scope.permissions = permissions;
      $scope.addMode = false;
      $scope.userCPRole = {};
      loadPvs();

      checkAllSitePermission();
      createSiteCpMap();
    }

    function createSiteCpMap() {
      $scope.siteCps = {};
      angular.forEach($scope.permissions, function(permission) {
        updateSiteCPMap(permission);
      });
    }

    function updateSiteCPMap(permission) {
      if ($scope.siteCps[permission.site]) {
        $scope.siteCps[permission.site].push(permission.cp);
      } else {
        $scope.siteCps[permission.site] = [permission.cp];
      }

      if (permission.site == 'All') {
        var idx = $scope.sites.indexOf(permission.site);
        if (idx > -1) {
          $scope.sites.splice(idx, 1);
        }
      }
    }

    function removePermissionFromMap(permission) {
      var idx = $scope.siteCps[permission.site].indexOf(permission.cp);
      $scope.siteCps[permission.site].splice(idx, 1);
    }

    function loadPvs() {
      $scope.sites = PvManager.getSites();
      if ($scope.permissions.length == 0) {
        $scope.sites.splice(0, 0, all);
      }

      Role.list().then(
        function(roleList) {
          $scope.roles = [];
          angular.forEach(roleList, function(role) {
              $scope.roles.push(role.name);
          });
        }
      );
    }

    $scope.showAddPermission = function() {
      $scope.addMode = true;
      $scope.userCPRole = {};
      setSitePvs('add');
    }

    $scope.showEditPermission = function(permission, idx) {
      $scope.getCps(permission.site);
      $scope.userCPRole = angular.copy(permission);
      $scope.editPermissionIdx = idx;
      $scope.addMode = false;
      setSitePvs('edit');
    }

    $scope.savePermissions = function() {
      //Save permissions
      $scope.userCPRole.id = count++;
      $scope.permissions.push($scope.userCPRole);
      updateSiteCPMap($scope.userCPRole);
      $scope.userCPRole = {};
      $scope.addMode = false;
      checkAllSitePermission();

    }

    $scope.editPermissions = function() {
      // Update Permission
      var oldPermission = $scope.permissions[$scope.editPermissionIdx];
      $scope.permissions[$scope.editPermissionIdx] = $scope.userCPRole;
      removePermissionFromMap(oldPermission);
      updateSiteCPMap($scope.userCPRole);

      $scope.userCPRole = {};
      checkAllSitePermission();
    }

    $scope.removePermission = function(permission, index) {
      //Remove Permission
      $scope.permissions.splice(index, 1);
      removePermissionFromMap(permission);
      checkAllSitePermission();
    }

    $scope.revertEdit = function() {
      $scope.addMode = false;
      $scope.userCPRole = {};
    }

    // TODO: Need to refactor this method
    $scope.getCps = function(site) {
      // If All Site is selected then CP dropdown should show only "All" protocols.
      var cps = $scope.siteCps[site];
      var idx = cps ? cps.indexOf(all) : -1;
      $scope.cps = undefined;
      $scope.cps = idx > -1 ? [] : (site == all) ? [all] : undefined;

      if (!$scope.cps) {
        $scope.userCPRole.cp = undefined;
        var cps = $scope.siteCps[site];
        CollectionProtocol.getCps(site).then( function(list) {
          var cpList = angular.copy(list);
          if (cps && cps.length > 0) {
            angular.forEach(cps, function(cp) {
              var idx = cpList.indexOf(cp);
              if (idx > -1) {
                cpList.splice(idx,1);
              }
            });
            $scope.cps = cpList;
          }
          else {
            $scope.cps = angular.copy(cpList);
            if ($scope.cps[0] != all) {
              $scope.cps.splice(0, 0, all);
            }
          }
        });
      }

    }

    function checkAllSitePermission() {
      $scope.allSitePermission = ($scope.permissions.length == 1 && $scope.permissions[0].site == 'All') ? true : false;
    }

    function setSitePvs(operation) {
      if ( ($scope.permissions.length > 0 && operation != 'edit') || ($scope.permissions.length > 1 && operation == 'edit')) {
        if ($scope.sites[0] == all) {
          $scope.sites.splice(0, 1);
        }
      } else {
        if ($scope.sites[0] != all) {
          $scope.sites.splice(0, 0, all);
        }
      }
    }

    init();

  });