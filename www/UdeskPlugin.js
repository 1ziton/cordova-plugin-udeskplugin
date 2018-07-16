var exec = require('cordova/exec');

exports.uDeskMethod = function(arg0, success, error) {
    exec(success, error, "udeskplugin", "uDeskMethod", [arg0]);
};
