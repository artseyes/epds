var commentWrapper = require('./commentWrapper.js');

module.exports = {
// Create standard comments header for minified files
    createComments: function (gutil) {
        var comments = [
            'Mohammed Amer Hussaini',
            'Copyright 2015',
            'GAO EPDS',
            'Compiled on ' + gutil.date('mmm d, yyyy h:MM:ss TT Z')
        ];
        return commentWrapper.wrap(comments);
    }
};
