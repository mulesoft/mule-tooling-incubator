'use strict';

module.exports = function ( grunt ) {

    grunt.initConfig({

        pkg: grunt.file.readJSON('package.json'),

        meta: {
            serverSrc: ['app/server/**/*.js'],
            libSrc: ['lib/**/*.js'],
            clientSrc: ['app/client/scripts/src/**/*.js'],
            clientStyles: ['app/client/styles/src/**/*.less']
        },

        jshint: {
            options: {jshintrc: '.jshintrc'},

            misc: ['Gruntfile.js'],
            server: ['<%=meta.serverSrc%>', '<%=meta.libSrc%>'],
            client: ['<%=meta.clientSrc%>']
        },


        // Tasks to generate the "client bundle"
        // -------------------------------------

        browserify: {
            vendor: {
                src: [
                    'app/client/scripts/lib/angular/angular.js'
                ],
                dest: 'app/client/scripts/vendor.js',
                options: {
                    shim: {
                        angular: {
                            path: 'app/client/scripts/lib/angular/angular.js',
                            exports: 'angular'
                        }
                    }
                }
            },

            app: {
                src: ['<%=meta.clientSrc%>'],
                dest: 'app/client/scripts/app.js',
                options: {
                    external: ['angular']
                }
            }
        },

        concat: {
            dev: {
                src: [
                    'app/client/scripts/livereload-support.js',
                    'app/client/scripts/vendor.js',
                    'app/client/scripts/app.js'
                ],
                dest: 'app/client/scripts/all.js'
            },
            prod: {
                src: [
                    'app/client/scripts/vendor.js',
                    'app/client/scripts/app.js'
                ],
                dest: 'app/client/scripts/all.src.js'
            }
        },

        uglify: {
            all: {
                options: {
                    sourceMap: 'app/client/scripts/all.js.map',
                    sourceMappingURL: '/scripts/all.js.map',
                    sourceMapPrefix: 3,
                    sourceMapRoot: '/scripts/',
                },
                files: {
                    'app/client/scripts/all.js': ['app/client/scripts/all.src.js']
                }
            }
        },

        less: {
            dev: {
                files: {
                    'app/client/styles/all.css': ['app/client/styles/src/main.less']
                }
            },
            prod: {
                options: {
                    cleancss: true
                },
                files: {
                    'app/client/styles/all.css': ['<%=meta.clientStyles%>']
                }
            }
        },

        clean: {
            scripts: [
                'app/client/scripts/vendor.js',
                'app/client/scripts/app.js',
                'app/client/scripts/all.js',
                'app/client/scripts/all.js.map',
                'app/client/scripts/all.src.js'
            ],
            styles: [
                'app/client/styles/all.css'
            ],
            lib: [
                'app/client/scripts/lib'
            ]
        },


        // Tasks to support auto-reloading during development
        // --------------------------------------------------

        watch: {
            app: {
                files: ['<%=meta.clientSrc%>', '<%=meta.libSrc%>'],
                tasks: ['browserify:app', 'concat:dev'],
                options: {
                    livereload: true
                }
            },
            vendor: {
                files: ['app/client/scripts/lib/**/*.js'],
                tasks: ['client-dev'],
                options: {
                    livereload: true
                }
            },
            less: {
                files: ['<%=meta.clientStyles%>'],
                tasks: ['less:dev'],
                options: {
                    livereload: true
                }
            }
        },

        nodemon: {
            server: {
                options: {
                    file: './app/server/index.js',
                    watchedExtensions: ['js', 'json'],
                    watchedFolders: ['app/server', 'lib']
                }
            }
        },

        concurrent: {
            server: ['nodemon', 'watch'],
            options: {
                logConcurrentOutput: true
            }
        }

    });


    grunt.loadNpmTasks('grunt-browserify');
    grunt.loadNpmTasks('grunt-concurrent');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-nodemon');

    grunt.registerTask('default', ['jshint', 'client-dev']);
    grunt.registerTask('client-dev', ['browserify', 'concat:dev', 'less:dev']);
    grunt.registerTask('client', ['browserify', 'concat:prod', 'uglify:all', 'less:prod']);
    grunt.registerTask('server', ['default', 'concurrent']);

};