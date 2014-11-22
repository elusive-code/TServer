this.fn = (function (scope, options) {
    var ___v1ew = [];
    ___v1ew.push(
        "<div class=\"off-canvas-wrap\" data-offcanvas>\n    <div class=\"inner-wrap\">\n        <!-- Off Canvas Menu -->\n        <aside id=\"slideMenu\" class=\"left-off-canvas-menu\">\n            <ul class=\"off-canvas-list\">\n                <li><a class=\"menuItem\" href=\"#\">Contexts</a></li>\n                <li><a class=\"menuItem\" href=\"#\">Pipelines</a></li>\n                <li><a class=\"menuItem\" href=\"#\">Processes</a></li>\n            </ul>\n        </aside>\n        <nav class=\"tab-bar\">\n            <section class=\"left-small show-for-small\">\n                <a class=\"left-off-canvas-toggle menu-icon\"\n                   role=\"button\"\n                   aria-controls=\"slideMenu\"\n                   aria-expanded=\"false\"\n                   href=\"#slideMenu\"><span></span></a>\n            </section>\n            <section class=\"middle tab-bar-section\">\n                <h1 class=\"title\">TServer</h1>\n            </section>\n        </nav>\n        <!-- main content goes here -->\n        <section class=\"row full\">\n            <ul class=\"large-3 medium-4 columns hide-for-small side-nav\"\n                aria-hidden=\"true\">\n                <li><a href=\"#\">Contexts</a></li>\n                <li><a href=\"#\">Pipelines</a></li>\n                <li><a href=\"#\">Processes</a></li>\n            </ul>\n            <div class=\"large-9 medium-8 columns\">\n                content\n                <ts-input class=\"testInput\" type=\"text\" value=\"{inputValue}\"", can.view.pending({tagName: 'ts-input', scope: scope, options: options, subtemplate: function (scope, options) {
            var ___v1ew = [];
            ___v1ew.push(
                "\n                <div>");
            ___v1ew.push(
                can.view.txt(
                    1,
                    'div',
                    0,
                    this,
                    can.Mustache.txt(
                        {scope: scope, options: options},
                        null, {get: "inputValue"})));
            ___v1ew.push("</div>\n            </div>\n        </section>\n        <section class=\"row full\">\n        </section>\n        <!-- close the off-canvas menu -->\n        <a class=\"exit-off-canvas\"></a>\n    </div>\n</div>");
            ;
            return ___v1ew.join('')
        }})
    )
});
//# sourceURL=null.js