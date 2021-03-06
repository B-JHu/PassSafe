package b_jHu.passwordManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class SidebarMenuItem extends JLabel {

	public SidebarMenuItem(String text) {
		setText(text);

		setForeground(Color.WHITE);
		setBackground(Color.BLACK);
		setFont(new Font("Calibri", Font.PLAIN, 20));
		setBorder(new EmptyBorder(8,5,0,10));

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
				setOpaque(true);
				revalidate();
				repaint();
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setOpaque(false);
				revalidate();
				repaint();
			}
		});
	}
}

