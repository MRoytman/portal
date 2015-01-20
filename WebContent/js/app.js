/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

require([ "jquery-1.11.1", "jqwidgets/jqxcore", "jqwidgets/jqxbuttons" ], function( jQuery, jqxcore, jqxbuttons ) {
    $(document).ready(function() {
        $("#myButton").jqxButton({ width: '120px', height: '35px', theme: 'darkblue'});
    });
});