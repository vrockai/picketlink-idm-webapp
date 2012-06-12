jQuery.fn.validate = function(opts){
    opts = jQuery.extend({
        messageContainer:jQuery(''),
        messageContainerValid:jQuery(''),
        messageContainerInvalid:jQuery(''),
        classValid:"valid",
        classInvalid:"invalid",
        validateFunction:function(){
            return false;
        }
    },opts||{});
    
    opts.messageContainerValid.hide();
    opts.messageContainerInvalid.hide();
    
    var input = jQuery(this);
    
    input.keyup(function() {
        if (opts.validateFunction(input.val())) {
            input.removeClass(opts.classInvalid);
            input.addClass(opts.classValid);
            //opts.messageContainer.hide();
            opts.messageContainerValid.show();
            opts.messageContainerInvalid.slideUp();
        } else {
            input.removeClass(opts.classValid);
            input.addClass(opts.classInvalid);
            //opts.messageContainer.hide();
            opts.messageContainerValid.hide();
            opts.messageContainerInvalid.slideDown();
        }
    });
};


