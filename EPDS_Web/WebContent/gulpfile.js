var gulp        = require('gulp'),
    watch       = require('gulp-watch'),
    liveReload  = require('gulp-livereload'),
    concat      = require('gulp-concat'),
    ngAnnotate  = require('gulp-ng-annotate'),
    uglify      = require('gulp-uglify'),
    rename      = require('gulp-rename'),
    moment      = require('moment'),
    notify      = require('gulp-notify'),
    less        = require('gulp-less'),
    serve       = require('gulp-serve'),
    angularTemplateCache = require('gulp-angular-templatecache'),
    htmlmin = require('gulp-htmlmin'),
    addStream = require('add-stream'),
    angularFilesort = require('gulp-angular-filesort'),
    inject = require('gulp-inject');



var config = {
    htmltemplates:  [
        'scripts/**/*.htm*',
        'views/**/*.htm*'
    ],
    templateCache: {
        file: 'templates.js',
        options: {
            module: 'epdsApp.core',
            //root: 'app/',
            standAlone: false
        }
    },
    temp: './scripts/app'
};
require('gulp-help')(gulp, {
    description: 'Help listing.'
});

/*gulp.task('serve', 'A simple web server.', serve({
    root: ['client'],
    port: 3000
}));*/



// Gulp task for creating template cache
gulp.task('templatecache', function() {
    notify('Creating an AngularJS $templateCache');

    return gulp
        .src(config.htmltemplates)
        .pipe(htmlmin({ collapseWhitespace: true }))
        .pipe(angularTemplateCache(
            config.templateCache.file,
            config.templateCache.options
        ))
        .pipe(gulp.dest(config.temp));
});

function prepareTemplates() {
    return gulp
        .src(config.htmltemplates)
        .pipe(htmlmin({ collapseWhitespace: true }))
        .pipe(angularTemplateCache(
            config.templateCache.file,
            config.templateCache.options
        ));
}

gulp.task('uglify-js', 'Concat, Ng-Annotate, Uglify JavaScript into a single app.min.js.', function() {
    gulp.src([
        'scripts/app.js',
        'scripts/app/module-defs.js',

        'scripts/directives/**/*.js',
        'scripts/filters/**/*.js',
        'scripts/misc/**/*.js',
        'scripts/services/**/*.js',


        'scripts/app/core/**/*.js',
        'scripts/app/agency/**/*.js',
        'scripts/app/registration/**/*.js',
        'scripts/app/action-messages/**/*.js',
        'scripts/app/authentication/**/*.js',
        'scripts/app/sidebar/**/*.js',
        'scripts/app/error/**/*.js',
        'scripts/app/dashboard/**/*.js',
        'scripts/app/admin/**/*.js',
        'scripts/app/case-docket-sheet/**/*.js',
        'scripts/app/protest/**/*.js',
        'scripts/app/submit-new-docs/**/*.js',
        'scripts/app/request-to-intervene/**/*.js',
        'scripts/app/account-update/**/*.js',
        'scripts/app/advance-search/**/*.js',
        'scripts/app/case-docket-file-info/**/*.js',
        'scripts/app/other-protests/**/*.js',
        'scripts/app/parties/**/*.js',
        'scripts/app/user-guides/**/*.js',

         // 'scripts/app/**/*.js',

        ]) //'client/js/source/**/*.js','scripts/app/**/*.js'

        .pipe(addStream.obj(prepareTemplates()))
        .pipe(concat('app'))
       // .pipe(ngAnnotate())

        .pipe(ngAnnotate({
            // true helps add where @ngInject is not used. It infers.
            // Doesn't work with resolve, so we must be explicit there
            add: true
        }))
        .on('error', notify.onError("Error: <%= error.message %>"))
        .pipe(uglify())
        //.on('error', notify.onError("Error: <%= error.message %>"))
        .pipe(rename({
            extname: ".min.js"
        }))
        .pipe(gulp.dest('client/js'))
        .pipe(notify('Uglified JavaScript (' + moment().format('MMM Do h:mm:ss A') + ')'))
        /*.pipe(liveReload({
            auto: false
        }))*/;
});

gulp.task('less', 'Compile less into a single app.css.', function() {
    gulp.src(['client/styles/bootstrap/bootstrap.less', 'client/styles/*.less'])
        .pipe(concat('app'))
        .pipe(less())
        .on('error', notify.onError("Error: <%= error.message %>"))
        .pipe(gulp.dest('client/styles'))
        .pipe(notify('Compiled less (' + moment().format('MMM Do h:mm:ss A') + ')'))
        .pipe(liveReload({
            auto: false
        }));
});

gulp.task('concat-uglify', ['templatecache', 'uglify-js']);
/*gulp.task('watch', 'Watch for changes and live reloads Chrome. Requires the Chrome extension \'LiveReload\'.', function() {
    liveReload.listen();
    watch({
        glob: 'client/js/source/!**!/!*.js'
    }, function() {
        gulp.start('uglify-js');
    });

    watch({
        glob: 'client/styles/!*.less'
    }, function() {
        gulp.start('less');
    });

    watch({
        glob: 'client/views/!**!/!*.html'
    }).pipe(liveReload({
        auto: false
    }));
});*/

gulp.task('default', ['watch', 'serve']);