define([
    'jquery',
    'can',
    'text!js/template/TServerApp.hbs',
    'can/view/stache',
    'js/component/TSInput',
    'js/component/ContextsPanel',
    'js/component/FilesPanel',
    'js/component/PipelinesPanel',
    'js/component/ProcessesPanel'
],function($,can,template){
    return can.Component.extend({
        tag: 'tserver-app',
        template: can.stache(template),
        init: function(element,parent){
            console.log('tserver-app init');
        },
        scope: {
            inputValue: 'testing input',
            panels: new can.List()
        },
        events: {
            '.menuItem click': function(element,event){
                $('.menuItem',this.element).each(function(index,el){
                    $(el).data('component').scope.toggle(false);
                });
                element.data('component').scope.toggle(true);
            }
        }
    })
});