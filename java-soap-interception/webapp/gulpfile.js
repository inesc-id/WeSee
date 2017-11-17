var gulp = require('gulp');

gulp.task('default', ['copyResources'], function() {
    return gulp.src([
            'graph.js', 
        ])
        .pipe(gulp.dest('../webServer/src/main/resources/static'));
});

gulp.task('copyResources', function() {
    return gulp.src([
            './node_modules/d3/build/d3.js',
            './bower_components/jquery/dist/jquery.js'
        ])
        .pipe(gulp.dest('../webServer/src/main/resources/static/libs'));
});