/**
 * This jQuery plugin displays pagination links inside the selected elements.
 *
 * @author Gabriel Birke (birke *at* d-scribe *dot* de)
 * @version 1.2
 * @param {int} maxentries Number of entries to paginate
 * @param {Object} opts Several options (see README for documentation)
 * @return {Object} jQuery Object
 */
jQuery.fn.pagination = function(opts){
    
    opts = jQuery.extend({
        maxentries:5,
        items_per_page:5,
        num_display_entries:10,
        current_page:0,
        num_edge_entries:0,
        link_to:"#",
        prev_text:"Prev",
        first_text:"First",
        next_text:"Next",
        last_text:"Last",
        ellipse_text:"...",
        prev_show_always:true,
        next_show_always:true,
        callback:function(){
            return false;
        }        
    },opts||{});
        
    var panel = jQuery(this);

    // Extract current_page from options
    var current_page = opts.current_page;    
        
    return this.each(function() {
        
        /**
		 * Calculate the maximum number of pages
		 */
        function numPages() {
            return Math.ceil(opts.maxentries/opts.items_per_page);
        }
		
        /**
		 * Calculate start and end point of pagination links depending on 
		 * current_page and num_display_entries.
		 * @return {Array}
		 */
        function getInterval()  {
            var ne_half = Math.ceil(opts.num_display_entries/2);
            var np = numPages();
            var upper_limit = np-opts.num_display_entries;
            var start = current_page>ne_half?Math.max(Math.min(current_page-ne_half, upper_limit), 0):0;
            var end = current_page>ne_half?Math.min(current_page+ne_half, np):Math.min(opts.num_display_entries, np);
            return [start,end];
        }
		
        /**
		 * This is the event handling function for the pagination links. 
		 * @param {int} page_id The new page number
		 */
        function pageSelected(page_id, evt){
            current_page = page_id;
            drawLinks();
            var continuePropagation = opts.callback(page_id, panel);            
            if (!continuePropagation) {
                if (evt.stopPropagation) {
                    evt.stopPropagation();
                }
                else {
                    evt.cancelBubble = true;
                }
            }
            return continuePropagation;
        }
		
        /**
		 * This function inserts the pagination links into the container element
		 */
        function drawLinks() {
       
            panel.empty();
            var interval = getInterval();
            var np = numPages();
            
            // This helper function returns a handler function that calls pageSelected with the right page_id
            var getClickHandler = function(page_id) {
                return function(evt){
                    return pageSelected(page_id,evt);
                };
            };
            // Helper function for generating a single link (or a span tag if it's the current page)
            var appendItem = function(page_id, appendopts){
                page_id = page_id<=0?0:(page_id<=np-1?page_id:np-1); // Normalize page id to sane value
                appendopts = jQuery.extend({
                    text:page_id+1, 
                    classes:""
                }, appendopts||{});
                
                var lnk;
                
                var np = numPages();
                
                if((page_id == current_page)||(np == 0)){
                    lnk = jQuery("<span class='current'>"+(appendopts.text)+"</span>");
                }
                else
                {
                    lnk = jQuery("<a>"+(appendopts.text)+"</a>")
                    .bind("click", getClickHandler(page_id))
                    .attr('href', opts.link_to.replace(/__id__/,page_id));
                }
                if(appendopts.classes){
                    lnk.addClass(appendopts.classes);
                }
                                                                
                panel.append(lnk);
            };
            
            // Generate "First"-Link
            appendItem(0,{
                text:opts.first_text, 
                classes:"first"
            });
            
            // Generate "Previous"-Link
            if(opts.prev_text && (current_page > 0 || opts.prev_show_always)){
                appendItem(current_page-1,{
                    text:opts.prev_text, 
                    classes:"prev"
                });
            }
            // Generate starting points            
            if (interval[0] > 0 && opts.num_edge_entries > 0)
            {
                var end = Math.min(opts.num_edge_entries, interval[0]);
                for(var i=0; i<end; i++) {
                    appendItem(i);
                }
                if(opts.num_edge_entries < interval[0] && opts.ellipse_text)
                {
                    jQuery("<span>"+opts.ellipse_text+"</span>").appendTo(panel);
                                        
                }
            }
            
            // Generate interval links
            for(var i=interval[0]; i<interval[1]; i++) {
                appendItem(i);
            }
            // Generate ending points
            if (interval[1] < np && opts.num_edge_entries > 0)
            {
                if(np-opts.num_edge_entries > interval[1]&& opts.ellipse_text)
                {
                    jQuery("<span>"+opts.ellipse_text+"</span>").appendTo(panel);
                                        
                }
                var begin = Math.max(np-opts.num_edge_entries, interval[1]);
                for(var i=begin; i<np; i++) {
                    appendItem(i);
                }
				
            }
            // Generate "Next"-Link
            if(opts.next_text && (current_page < np-1 || opts.next_show_always)){
                appendItem(current_page+1,{
                    text:opts.next_text, 
                    classes:"next"
                });
            }
                        
            // Generate "Last"-Link
            appendItem(np-1,{
                text:opts.last_text,                 
                classes:"last"
            });
                        
            var lnkRefresh = jQuery("<a>Refresh</a>");
            lnkRefresh.addClass("refresh");
            lnkRefresh.bind("click", getClickHandler(current_page));
            panel.append(lnkRefresh);
            
            var lnkReset = jQuery("<a>Reset</a>");
            lnkReset.addClass("reset");
            lnkReset.bind("click", getClickHandler(0));
            panel.append(lnkReset);
       
            panel.children("a").button();            
            panel.children(".refresh").button({icons: {primary: "ui-icon-refresh"}});
            panel.children(".reset").button({icons: {primary: "ui-icon-home"}});
            panel.children(".prev").button({icons: {primary: "ui-icon-arrow-1-w"}});
            panel.children(".next").button({icons: {primary: "ui-icon-arrow-1-e"}});
            panel.children(".first").button({icons: {primary: "ui-icon-arrowstop-1-w"}});
            panel.children(".last").button({icons: {primary: "ui-icon-arrowstop-1-e"}});
            
            panel.children("span").button({disabled: true});
        }

        // Create a sane value for opts.maxentries and items_per_page
        opts.maxentries = (!opts.maxentries || opts.maxentries < 0)?1:opts.maxentries;
        opts.items_per_page = (!opts.items_per_page || opts.items_per_page < 0)?1:opts.items_per_page;
        // Store DOM element for easy access from all inner functions

        // Attach control functions to the DOM element 
        this.selectPage = function(page_id){
            pageSelected(page_id);
        };
        this.prevPage = function(){ 
            if (current_page > 0) {
                pageSelected(current_page - 1);
                return true;
            }
            else {
                return false;
            }
        };
        this.nextPage = function(){ 
            if(current_page < numPages()-1) {
                pageSelected(current_page+1);
                return true;
            }
            else {
                return false;
            }
        };
        // When all initialisation is done, draw the links
        drawLinks();        
    });
};


