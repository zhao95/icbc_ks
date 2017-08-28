/*
 * printThis v1.5
 * @desc Printing plug-in for jQuery
 * @author Jason Day
 *
 * Resources (based on) :
 *              jPrintArea: http://plugins.jquery.com/project/jPrintArea
 *              jqPrint: https://github.com/permanenttourist/jquery.jqprint
 *              Ben Nadal: http://www.bennadel.com/blog/1591-Ask-Ben-Print-Part-Of-A-Web-Page-With-jQuery.htm
 *
 * Licensed under the MIT licence:
 *              http://www.opensource.org/licenses/mit-license.php
 *
 * (c) Jason Day 2015
 *
 * Usage:
 *
 *  $("#mySelector").printThis({
 *      debug: false,               * show the iframe for debugging
 *      importCSS: true,            * import page CSS
 *      importStyle: false,         * import style tags
 *      printContainer: true,       * grab outer container as well as the contents of the selector
 *      loadCSS: "path/to/my.css",  * path to additional css file - us an array [] for multiple
 *      pageTitle: "",              * add title to print page
 *      removeInline: false,        * remove all inline styles from print elements
 *      printDelay: 333,            * variable print delay
 *      header: null,               * prefix to html
 *      formValues: true            * preserve input/form values
 *  });
 *
 * Notes:
 *  - the loadCSS will load additional css (with or without @media print) into the iframe, adjusting layout
 */
/*
$("#mySelector").printThis({
	debug: false,      
	loadCSS: "path/to/my.css",       
	printDelay: 333         
});
*/
;
(function($) {
    var opt;
    $.fn.printThis = function(options) {
        opt = $.extend({}, $.fn.printThis.defaults, options);
        var $element = this instanceof jQuery ? this : $(this);

        var strFrameName = "printThis-" + (new Date()).getTime();

        if (window.location.hostname !== document.domain && navigator.userAgent.match(/msie/i)) {
            // Ugly IE hacks due to IE not inheriting document.domain from parent
            // checks if document.domain is set by comparing the host name against document.domain
            var iframeSrc = "javascript:document.write(\"<head><script>document.domain=\\\"" + document.domain + "\\\";</script></head><body></body>\")";
            var printI = document.createElement('iframe');
            printI.name = "printIframe";
            printI.id = strFrameName;
            printI.className = "MSIE";
            document.body.appendChild(printI);
            printI.src = iframeSrc;

        } else {
            // other browsers inherit document.domain, and IE works if document.domain is not explicitly set
            var $frame = $("<iframe id='" + strFrameName + "' name='printIframe' />");
            $frame.appendTo("body");
        }


        var $iframe = $("#" + strFrameName);

        // show frame if in debug mode
        if (!opt.debug) {
        	$iframe.css({
                position: "absolute",
                width: "0px",
                height: "0px",
                left: "0px",
                top: "0px"
            });
        } else {
        	$iframe.css({
                width: "804px",
                height: "1123px",
                background:"#ccc",
                margin:"20px 160px",
                border:"0"
            });
        }


        // $iframe.ready() and $iframe.load were inconsistent between browsers    
        setTimeout(function() {

            // Add doctype to fix the style difference between printing and render
            function setDocType($iframe,doctype){
                var win, doc;
                win = $iframe.get(0);
                win = win.contentWindow || win.contentDocument || win;
                doc = win.document || win.contentDocument || win;
                doc.open();
                doc.write(doctype);
                doc.close();
            }
            if(opt.doctypeString){
                setDocType($iframe,opt.doctypeString);
            }

            var $doc = $iframe.contents(),
                $head = $doc.find("head"),
                $body = $doc.find("body");

            // add base tag to ensure elements use the parent domain
            $head.append('<base href="' + document.location.protocol + '//' + document.location.host + '">');

            // import page stylesheets
            if (opt.importCSS) $("link[rel=stylesheet]").each(function() {
                var href = $(this).attr("href");
                if (href) {
                    var media = $(this).attr("media") || "all";
                    $head.append("<link type='text/css' rel='stylesheet' href='" + href + "' media='" + media + "'>")
                }
            });
            
            // import style tags
            if (opt.importStyle) $("style").each(function() {
                $(this).clone().appendTo($head);
                //$head.append($(this));
            });

            //add title of the page
            if (opt.pageTitle) $head.append("<title>" + opt.pageTitle + "</title>");

            // import additional stylesheet(s)
            if (opt.loadCSS) {
               if( $.isArray(opt.loadCSS)) {
                    jQuery.each(opt.loadCSS, function(index, value) {
                       $head.append("<link type='text/css' rel='stylesheet' href='" + this + "'>");
                    });
                } else {
                    $head.append("<link type='text/css' rel='stylesheet' href='" + opt.loadCSS + "'>");
                }
            }

            // print header
            if (opt.header) $body.append(opt.header);

            // grab $.selector as container
            if (opt.printContainer) $body.append($element.outer());

            // otherwise just print interior elements of container
            else $element.each(function() {
                $body.append($(this).html());
            });

            // capture form/field values
            if (opt.formValues) {
                // loop through inputs
                var $input = $element.find('input');
                if ($input.length) {
                    $input.each(function() {
                        var $this = $(this),
                            $name = $(this).attr('name'),
                            $checker = $this.is(':checkbox') || $this.is(':radio'),
                            $iframeInput = $doc.find('input[name="' + $name + '"]'),
                            $value = $this.val();

                        //order matters here
                        if (!$checker) {
                            $iframeInput.val($value);
                        } else if ($this.is(':checked')) {
                            if ($this.is(':checkbox')) {
                                $iframeInput.attr('checked', 'checked');
                            } else if ($this.is(':radio')) {
                                $doc.find('input[name="' + $name + '"][value=' + $value + ']').attr('checked', 'checked');
                            }
                        }

                    });
                }

                //loop through selects
                var $select = $element.find('select');
                if ($select.length) {
                    $select.each(function() {
                        var $this = $(this),
                        	$id = $(this).attr('id'),
                        	$text = $this.find("option:selected").text(),
                            $value = $this.val();
                        
                        //$doc.find('select[id="' + $id + '"]').val($value);
                        //柴志强：把下拉框改成输入框显示
                        var t = $('<input type="text" class="ui-text-default disabled" readonly="readonly">').attr({
                        	"id":$id,
                        	"name":$id
                        }).css({
                        	"text-align":"left",
                        	"width":"100%"
                        }).val($text);
                        $doc.find('select[id="' + $id + '"]').replaceWith(t);
                    });
                }

                //loop through textareas
                var $textarea = $element.find('textarea');
                if ($textarea.length) {
                    $textarea.each(function() {
                        var $this = $(this),
                            $name = $(this).attr('name'),
                            $value = $this.val();
                        $doc.find('textarea[name="' + $name + '"]').val($value);
                    });
                }
            } // end capture form/field values

            // remove inline styles
            if (opt.removeInline) {
                // $.removeAttr available jQuery 1.7+
                if ($.isFunction($.removeAttr)) {
                    $doc.find("body *").removeAttr("style");
                } else {
                    $doc.find("body *").attr("style", "");
                }
            }
            
            //柴志强：import excute JS(s), this --> $body
            if (opt.loadJS) {
               if ($.isArray(opt.loadJS)) {
                    jQuery.each(opt.loadJS, function(index, jsPath) {
                    	Load.js(jsPath, $body);
                    });
                } else {
                	Load.js(opt.loadJS, $body);
                }
            }
            
            //设置网页打印的页眉页脚为空
            (function() {
                var hkey_root="HKEY_CURRENT_USER";
                var hkey_path="\\Software\\Microsoft\\Internet Explorer\\PageSetup\\";
            	try{
            		var RegWsh = new ActiveXObject("WScript.Shell");
            		var hkey_key="header";
            		RegWsh.RegWrite(hkey_root+hkey_path+hkey_key,"");
            		hkey_key="footer";
            		RegWsh.RegWrite(hkey_root+hkey_path+hkey_key,"");
            		hkey_key="margin_bottom";
        		    RegWsh.RegWrite(hkey_root+hkey_path+hkey_key,"0"); //0.39相当于把页面设置里面的边距设置为10
        		    hkey_key="margin_left";
        		    RegWsh.RegWrite(hkey_root+hkey_path+hkey_key,"0");
        		    hkey_key="margin_right";
        		    RegWsh.RegWrite(hkey_root+hkey_path+hkey_key,"0");
        		    hkey_key="margin_top";
        		    RegWsh.RegWrite(hkey_root+hkey_path+hkey_key,"0");
        		    hkey_key="Shrink_To_Fit";
        		    RegWsh.RegWrite(hkey_root+hkey_path+hkey_key,"no");
        		    hkey_key="Print_Background";
        		    RegWsh.RegWrite(hkey_root+hkey_path+hkey_key,"yes");
            	}catch(e){}
			})(); 

            setTimeout(function() {
                if ($iframe.hasClass("MSIE")) {
                    // check if the iframe was created with the ugly hack
                    // and perform another ugly hack out of neccessity
                    window.frames["printIframe"].focus();
                    $head.append("<script>  window.print(); </script>");
                } else {
                    // proper method
                    if (document.queryCommandSupported("print")) {
                        $iframe[0].contentWindow.document.execCommand("print", false, null);
                    } else {
                        $iframe[0].contentWindow.focus();
                        $iframe[0].contentWindow.print();
                    }
                }

                //remove iframe after print
                if (!opt.debug) {
                    setTimeout(function() {
                        $iframe.remove();
                    }, 1000);
                }

            }, opt.printDelay);

        }, 333);

    };

    // defaults
    $.fn.printThis.defaults = {
        debug: false,           // show the iframe for debugging
        importCSS: true,        // import parent page css
        importStyle: true,     // import style tags
        printContainer: true,   // print outer container/$.selector
        loadCSS: "",            // load an additional css file - load multiple stylesheets with an array []
        loadJS:"",              // 在打印之前对页面进行调整的JS
        pageTitle: "",          // add title to print page
        removeInline: false,    // remove all inline styles
        printDelay: 333,        // variable print delay
        header: null,           // prefix to html
        formValues: true,        // preserve input/form values
        doctypeString: '<!DOCTYPE html>' // html doctype
    };

    // $.selector container
    jQuery.fn.outer = function() {
        return $($("<div></div>").html(this.clone())).html()
    }
})(jQuery);