app.service('messageService', [function () {
    return {
        // Dialogs
        showWarning : function (message) {
            BootstrapDialog.show({
                type : BootstrapDialog.TYPE_WARNING,
                title :"ROCKIT",
                message : message,
                onshown: function(dialogRef) {
                    dialogRef.getButton('close').focus();
                },
                buttons : [{
                    id: 'close',
                    label : 'Close',
                    action : function (dialogRef) {
                        dialogRef.close();
                    }
                }
                ]
            });
        },
        showError : function (message) {
            BootstrapDialog.show({
                type : BootstrapDialog.TYPE_DANGER,
                title : "ROCKIT",
                message : message,
                onshown: function(dialogRef) {
                    dialogRef.getButton('close').focus();
                },
                buttons : [{
                    id: 'close',
                    label : 'Close',
                    action : function (dialogRef) {
                        dialogRef.close();
                    }
                }
                ]
            });
        },
        showInfo : function (message) {
            BootstrapDialog.show({
                type : BootstrapDialog.TYPE_INFO,
                title : "ROCKIT",
                message : message,
                onshown: function(dialogRef) {
                    dialogRef.getButton('close').focus();
                },
                buttons : [{
                    id: 'close',
                    label : 'Close',
                    action : function (dialogRef) {
                        dialogRef.close();
                    }
                }
                ]
            });
        }
    }
}
]);