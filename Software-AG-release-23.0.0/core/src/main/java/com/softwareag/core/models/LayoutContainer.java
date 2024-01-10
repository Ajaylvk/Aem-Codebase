package com.softwareag.core.models;

import java.util.List;
import com.adobe.cq.wcm.core.components.models.Component;
import org.osgi.annotation.versioning.ConsumerType;
/**
* The LayoutContainer model.
*/
@ConsumerType
public interface LayoutContainer extends Component {

                /**

                 * Gets the count of columns.

                 *

                 * @return the counter count

                 */
                String getColumnCount();

                /**

                 * Returns a list of columns

                 *

                 * @return returns the list of columns with size as per column count

                 */

               // List<String> getColumnList();
			   String getBackgroundColor();

				String getTitle();
				
				String getDescription();
				
				String getNoMarginLeft();

				String getAnimation();
				

}
