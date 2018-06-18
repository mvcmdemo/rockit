app.controller('SendMessageController', function ($uibModalInstance, $scope, user) {
    $scope.user = angular.copy(user);
    $scope.send = function() {
        var message = $('#textMessage').val();
        window.open('mailto:' + user.email + '?subject=ROCKit message&body=' + message);
        $uibModalInstance.dismiss('cancel');
    };

    $scope.cancel = function(){
        $uibModalInstance.dismiss('cancel');
    }
});