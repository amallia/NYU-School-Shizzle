function fetchSBSExamples(A){var B="/data/sbsExamples.json";$.getJSON(B,function(G){var C=parseList(A);var E="";if(C.length<4){E=getDefaultCategory(E);C.push(E)}var F;for(var D=0;D<C.length;D++){F=C[D].trim();if(!G[F]){F=getDefaultCategory(E)}appendCategoryName(G[F].name,D+1);switch(D){case 0:appendExamples(G[F].examples,3,1,G[F].fileNames);break;case 1:appendExamples(G[F].examples,2,4,G[F].fileNames);break;case 2:appendExamples(G[F].examples,2,6,G[F].fileNames);break;case 3:appendExamples(G[F].examples,1,8,G[F].fileNames);break}}}).fail(function(){console.error("Error processing JSON file: "+B+" for interactive SBS popup examples.")})}function buildSBSExamples(A){if(A.indexOf("StepByStepGeneral")>-1){buildSBSGeneral(A)}else{if(A.indexOf("GetSBSRelatedExamples")>-1){buildSBSDefaults()}else{fetchSBSExamples(A)}}}function buildSBSGeneral(A){var B=parseList(A);if(B[1]){fetchSBSExamples("{Calculus,Algebra,LinearAlgebra,"+B[1]+"}")}else{buildSBSDefaults()}}function buildSBSDefaults(){fetchSBSExamples("{Calculus,Algebra,LinearAlgebra,Chemistry}")}function getDefaultCategory(B){var E="{Calculus,Algebra,LinearAlgebra,Chemistry}";var A=parseList(E);var C=Math.floor(Math.random()*A.length);var F=A[C];if(B==F){var D=(C+1)%A.length;F=A[D]}return F}function appendExamples(E,D,B,A){for(var C=0;C<D;C++){appendInput(E[C],B+C);appendImage(A[C],B+C)}}function parseList(B){var A=B.replace("{","");A=A.replace("}","");return A.split(",")}function appendInput(A,B){$("#input"+B).text(A)}function appendCategoryName(B,A){$("#category"+A).text(B)}function appendImage(B,A){$("#sbsImg"+A).attr("src","/images/sbs-popup/"+B)};