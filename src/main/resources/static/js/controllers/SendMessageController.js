app.controller('SendMessageController', function ($uibModalInstance, $scope, user) {
    $scope.user = angular.copy(user);
    $scope.send = function() {
        var message = $('#textMessage').val();
        $uibModalInstance.dismiss('cancel');
        alert('The message have been sent');
    };

    $scope.cancel = function(){
        $uibModalInstance.dismiss('cancel');
    }
});