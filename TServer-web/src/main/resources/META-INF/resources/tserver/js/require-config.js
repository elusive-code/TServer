(function () {
    var cfg = {
        baseUrl: '/tserver/js',
        paths: {
            //requirejs plugins
            text: 'bower/requirejs-text/text',
            jade: 'bower/require-jade/jade',

            //libs
            jquery: 'bower/jquery/dist/jquery',
            underscore: 'bower/underscore/underscore',
            q: 'bower/q/q',
            gss: 'bower/gss/dist/gss/gss',
            gss_worker: 'bower/gss/dist/worker',

            "foundation": 'bower/foundation/js/foundation',
            "foundation.abide": 'foundation/foundation.abide',
            "foundation.accordion": 'foundation/foundation.accordion',
            "foundation.alert": 'foundation/foundation.alert',
            "foundation.clearing": 'foundation/foundation.clearing',
            "foundation.dropdown": 'foundation/foundation.dropdown',
            "foundation.equalizer": 'foundation/foundation.equalizer',
            "foundation.interchange": 'foundation/foundation.interchange',
            "foundation.joyride": 'foundation/foundation.joyride',
            "foundation.magellan": 'foundation/foundation.magellan',
            "foundation.offcanvas": 'foundation/foundation.offcanvas',
            "foundation.orbit": 'foundation/foundation.orbit',
            "foundation.reveal": 'foundation/foundation.reveal',
            "foundation.slider": 'foundation/foundation.slider',
            "foundation.tab": 'foundation/foundation.tab',
            "foundation.toolbar": 'foundation/foundation.toolbar',
            "foundation.topbar": 'foundation/foundation.topbar',

            //frameworks
            can:'bower/canjs/amd-dev/can',
            backbone: 'bower/backbone/backbone',
            angular: 'bower/angular/angular',

            ace: 'bower/ace/lib/ace'
        },
        shim: {
            "jquery.cookie": ['jquery'],
            "foundation": ['jquery'],
            "foundation.abide": ['foundation'],
            "foundation.accordion": ['foundation'],
            "foundation.alert": ['foundation'],
            "foundation.clearing": ['foundation'],
            "foundation.dropdown": ['foundation'],
            "foundation.equalizer": ['foundation'],
            "foundation.interchange": ['foundation'],
            "foundation.joyride": ['foundation', 'jquery.cookie'],
            "foundation.magellan": ['foundation'],
            "foundation.offcanvas": ['foundation'],
            "foundation.orbit": ['foundation'],
            "foundation.reveal": ['foundation'],
            "foundation.slider": ['foundation'],
            "foundation.tab": ['foundation'],
            "foundation.toolbar": ['foundation'],
            "foundation.topbar": ['foundation']
        }
    };

    if (typeof require !='undefined' && require) {
        require.config(cfg);
    } else {
        require = cfg;
    }
})();