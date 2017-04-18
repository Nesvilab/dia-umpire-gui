/*
 * Copyright 2017 Dmitry Avtonomov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package umich.msfragger.gui.renderers;

import java.awt.Component;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Dmitry Avtonomov
 */
public class TableCellDoubleRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    private final int precision;
    private Number numberValue;
    private NumberFormat nf;
    private DecimalFormat df0;

    public TableCellDoubleRenderer(int aPrecision) {
        super();
        precision = aPrecision;
        setHorizontalAlignment(SwingConstants.LEFT);
        nf = NumberFormat.getNumberInstance();
        nf.setMinimumFractionDigits(aPrecision);
        nf.setMaximumFractionDigits(aPrecision);
        df0 = new DecimalFormat("#.##########");
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        // First format the cell value as required
        if (value != null) {
            if (value instanceof Double) {
                value = df0.format((Double)value);
            } else if (value instanceof Number) {
                value = nf.format(value);
            }
        }

        // And pass it on to parent class
        return super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
    }
}
