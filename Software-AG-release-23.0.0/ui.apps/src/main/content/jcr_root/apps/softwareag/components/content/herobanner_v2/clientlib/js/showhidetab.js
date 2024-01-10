


(function(document, $) {
    "use strict";

    // when dialog gets injected
    $(document).on("foundation-contentloaded", function(e) {
        // if there is already an inital value make sure the according target element becomes visible

        showHide($(".cq-dialog-tab-showhide", e.target));
    });

    $(document).on("selected", ".cq-dialog-tab-showhide", function(e) {
        showHide($(this));
    });
    
    $(document).on("change", ".cq-dialog-tab-showhide", function(e) {

        showHide($(this));
    });

    $(document).on("change", ".cq-dialog-dropdown-showhide", function(e){

        var dropdownVal = $(".cq-dialog-dropdown-showhide").val();

		//alert(dropdownVal);

    if(dropdownVal == "herobannera"){

        $(document).find("[name*='./heroBannerBackground']")[0].selectedIndex = 0;

        // $(".imageb-tab-showhide-target").addClass("hide");
				 $(document).find("[name*='./backgroundImageb']")[0].selectedIndex = 0;
           }
           if(dropdownVal == "herobannerb"){

            //    $(".image-tab-showhide-target").addClass("hide");
				 $(document).find("[name*='./backgroundImagea']")[0].selectedIndex = 0;
           }
      });


   function showHide(el){
       el.each(function(i, element) {
         /* get the selector to find the target elements. its stored as data-.. attribute */
         var target = $(element).data("cqDialogTabShowhideTarget");

 			var dropdownVal = $(".cq-dialog-dropdown-showhide").val();


           if ($(element).data("select")) {
			 if(dropdownVal == "herobannera"){

               // get the selected value
               var value =$(document).find("[name*='./backgroundImagea']").val();
               // make sure all unselected target elements are hidden.
               $(target).not(".hide").addClass("hide");

               /* show the target element that contains the selected value as data-showhidetargetvalue attribute */
                 if(value){

                 	$(target+'.'+value).removeClass("hide");
                 }
             }else if(dropdownVal == "herobannerb"){

                 $(".image-tab-showhide-target").addClass("hide");
	
               // get the selected value
               var value = $(document).find("[name*='./backgroundImageb']").val();

               // make sure all unselected target elements are hidden.
               $(target).not(".hide").addClass("hide");

               /* show the target element that contains the selected value as data-showhidetargetvalue attribute */
                 if(value){

                	 $(target+'.'+value).removeClass("hide");
                 }
             } else{
				  var value = $(element).data("select").getValue();
               // make sure all unselected target elements are hidden.
				if(value !== "select")
                  $(target).not(".hide").addClass("hide");

               /* show the target element that contains the selected value as data-showhidetargetvalue attribute */
                 if(value){
                 	$(target+'.'+value).removeClass("hide");
                 }
             }

           }else if($(element).is('input:checkbox')){
              // toggle the target element that contains the selected value as data-showhidetargetvalue attribute
              if($(element).is(':checked')){
                $(target).removeClass( "hide" );
              }else{
                $(target).addClass( "hide" );
              }
         }
       })
   }

})(document,Granite.$);

