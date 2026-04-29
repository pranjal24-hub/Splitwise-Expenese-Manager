import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

// Main class from where the GUI application starts
public class MainGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppData.loadAll();
            AutoSaveThread auto = new AutoSaveThread();
            auto.setDaemon(true);
            auto.start();
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new AuthFrame().setVisible(true);
        });
    }
}

// This class keeps common colors, fonts and reusable UI methods
class AppTheme {
    static final Color BG = new Color(15, 23, 42);
    static final Color CARD = new Color(30, 41, 59);
    static final Color CARD_2 = new Color(51, 65, 85);
    static final Color PANEL = new Color(17, 24, 39);
    static final Color ACCENT = new Color(99, 102, 241);
    static final Color ACCENT_2 = new Color(236, 72, 153);
    static final Color SUCCESS = new Color(34, 197, 94);
    static final Color WARN = new Color(245, 158, 11);
    static final Color DANGER = new Color(239, 68, 68);
    static final Color TEXT = new Color(241, 245, 249);
    static final Color MUTED = new Color(148, 163, 184);

    static Font h1() { return new Font("Segoe UI", Font.BOLD, 28); }
    static Font h2() { return new Font("Segoe UI", Font.BOLD, 18); }
    static Font body() { return new Font("Segoe UI", Font.PLAIN, 14); }
    static Font small() { return new Font("Segoe UI", Font.PLAIN, 12); }

    static Border pad(int t, int l, int b, int r) {
        return BorderFactory.createEmptyBorder(t, l, b, r);
    }

    static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        styleButton(b, ACCENT, TEXT);
        return b;
    }

    static JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        styleButton(b, CARD_2, TEXT);
        return b;
    }

    static JButton dangerButton(String text) {
        JButton b = new JButton(text);
        styleButton(b, DANGER, TEXT);
        return b;
    }

    static void styleButton(JButton b, Color bg, Color fg) {
        b.setFocusPainted(false);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setFont(h2());
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new CompoundBorder(new LineBorder(bg.brighter(), 1, true), pad(12, 18, 12, 18)));
    }

    static JTextField textField() {
        JTextField tf = new JTextField();
        styleTextField(tf);
        return tf;
    }

    static JPasswordField passwordField() {
        JPasswordField pf = new JPasswordField();
        styleTextField(pf);
        return pf;
    }

    static void styleTextField(JTextField tf) {
        tf.setBackground(new Color(248, 250, 252));
        tf.setForeground(Color.BLACK);
        tf.setCaretColor(Color.BLACK);
        tf.setFont(body());
        tf.setBorder(new CompoundBorder(new LineBorder(new Color(203, 213, 225), 1, true), pad(10, 12, 10, 12)));
    }

    static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(TEXT);
        l.setFont(body());
        return l;
    }

    static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(CARD);
        p.setBorder(new CompoundBorder(new LineBorder(new Color(71, 85, 105), 1, true), pad(18, 18, 18, 18)));
        return p;
    }

    static JScrollPane styledScroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.getViewport().setBackground(CARD);
        sp.setBorder(new LineBorder(new Color(71, 85, 105), 1, true));
        return sp;
    }

    static JTable table(String[] cols, Object[][] data) {
        DefaultTableModel model = new DefaultTableModel(data, cols) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable t = new JTable(model);
        t.setRowHeight(28);
        t.setBackground(Color.WHITE);
        t.setForeground(Color.BLACK);
        t.setGridColor(new Color(226, 232, 240));
        t.getTableHeader().setBackground(new Color(226, 232, 240));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setFont(body());
        return t;
    }
}

// Small helper methods used by different panels
class UIHelper {
    static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    static boolean confirm(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    static String today() { return LocalDate.now().toString(); }

    static java.util.List<Group> myGroups() {
        java.util.List<Group> list = new ArrayList<>();
        if (AppData.currentUser == null) return list;
        for (Group g : AppData.groups) {
            if (g.members.contains(AppData.currentUser.username)) list.add(g);
        }
        return list;
    }

    static String[] groupNames(java.util.List<Group> groups) {
        String[] arr = new String[groups.size()];
        for (int i = 0; i < groups.size(); i++) {
            Group g = groups.get(i);
            arr[i] = g.groupId + " - " + g.groupName;
        }
        return arr;
    }

    static String splitsAsText(Expense e) {
        StringBuilder sb = new StringBuilder();
        for (Split s : e.splits) {
            if (sb.length() > 0) sb.append(" | ");
            sb.append(s.username).append(": ").append(String.format("%.2f", s.amount));
        }
        return sb.toString();
    }

    static String settlementSummary(Settlement s) {
        return "#" + s.settlementId + " | Group " + s.groupId + " | " + s.fromUser + " -> " + s.toUser +
                " | Rs. " + String.format("%.2f", s.amount) + " | " + s.date + " | " + s.mode;
    }
}

// First window of the project: login and register screen
class AuthFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    AuthFrame() {
        setTitle("Splitwise Expense Manager");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(buildUI());
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new GridLayout(1, 2));
        root.setBackground(AppTheme.BG);

        JPanel left = new JPanel();
        left.setBackground(AppTheme.BG);
        left.setBorder(AppTheme.pad(60, 60, 60, 60));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("Expense Manager");
        brand.setForeground(AppTheme.TEXT);
        brand.setFont(AppTheme.h1());
        JLabel subtitle = new JLabel("A simple Java Swing project for managing shared and personal expenses");
        subtitle.setForeground(AppTheme.MUTED);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        left.add(brand);
        left.add(Box.createVerticalStrut(10));
        left.add(subtitle);
        left.add(Box.createVerticalStrut(30));

        left.add(featureCard("Group Expense Management", "Add group expenses using equal, exact or percentage split."));
        left.add(Box.createVerticalStrut(15));
        left.add(featureCard("Budget Management", "Set category budgets and check monthly spending."));
        left.add(Box.createVerticalStrut(15));
        left.add(featureCard("Reports and Recurring Expenses", "View monthly reports and save recurring expenses."));

        cardPanel.add(new LoginPanel(this), "login");
        cardPanel.add(new RegisterPanel(this), "register");
        cardPanel.setBorder(AppTheme.pad(50, 50, 50, 50));
        cardPanel.setBackground(AppTheme.PANEL);

        root.add(left);
        root.add(cardPanel);
        return root;
    }

    private JPanel featureCard(String title, String desc) {
        JPanel p = AppTheme.card();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel t = new JLabel(title);
        t.setForeground(AppTheme.TEXT);
        t.setFont(AppTheme.h2());
        JLabel d = new JLabel("<html><body style='width:300px'>" + desc + "</body></html>");
        d.setForeground(AppTheme.MUTED);
        d.setFont(AppTheme.body());
        p.add(t);
        p.add(Box.createVerticalStrut(8));
        p.add(d);
        return p;
    }

    void showLogin() { cardLayout.show(cardPanel, "login"); }
    void showRegister() { cardLayout.show(cardPanel, "register"); }

    void onLoginSuccess(User user) {
        AppData.currentUser = user;
        DashboardFrame frame = new DashboardFrame();
        frame.setVisible(true);
        dispose();
    }
}

// Login panel for existing users
class LoginPanel extends JPanel {
    LoginPanel(AuthFrame frame) {
        setBackground(AppTheme.PANEL);
        setLayout(new GridBagLayout());

        JPanel card = AppTheme.card();
        card.setPreferredSize(new Dimension(380, 420));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Login");
        title.setForeground(AppTheme.TEXT);
        title.setFont(AppTheme.h1());
        JLabel subtitle = new JLabel("Enter your username and password to continue");
        subtitle.setForeground(AppTheme.MUTED);
        subtitle.setFont(AppTheme.body());

        JTextField username = AppTheme.textField();
        JPasswordField password = AppTheme.passwordField();
        JButton login = AppTheme.primaryButton("Login");
        JButton goRegister = AppTheme.secondaryButton("Register New User");

        login.addActionListener(e -> {
            String u = username.getText().trim();
            String p = new String(password.getPassword()).trim();
            User user = AppData.findUserByUsername(u);
            if (u.isEmpty() || p.isEmpty()) {
                UIHelper.error(this, "Please enter username and password.");
                return;
            }
            if (user == null) {
                UIHelper.error(this, "User not found.");
                return;
            }
            if (!user.password.equals(p)) {
                UIHelper.error(this, "Wrong password.");
                return;
            }
            frame.onLoginSuccess(user);
        });

        goRegister.addActionListener(e -> frame.showRegister());

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(24));
        card.add(AppTheme.label("Username"));
        card.add(Box.createVerticalStrut(8));
        card.add(username);
        card.add(Box.createVerticalStrut(16));
        card.add(AppTheme.label("Password"));
        card.add(Box.createVerticalStrut(8));
        card.add(password);
        card.add(Box.createVerticalStrut(24));
        card.add(login);
        card.add(Box.createVerticalStrut(12));
        card.add(goRegister);

        add(card);
    }
}

// Register panel for creating a new user
class RegisterPanel extends JPanel {
    RegisterPanel(AuthFrame frame) {
        setBackground(AppTheme.PANEL);
        setLayout(new GridBagLayout());

        JPanel card = AppTheme.card();
        card.setPreferredSize(new Dimension(400, 480));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Register New User");
        title.setForeground(AppTheme.TEXT);
        title.setFont(AppTheme.h1());
        JLabel subtitle = new JLabel("Enter basic details to create your account");
        subtitle.setForeground(AppTheme.MUTED);
        subtitle.setFont(AppTheme.body());

        JTextField username = AppTheme.textField();
        JPasswordField password = AppTheme.passwordField();
        JTextField limit = AppTheme.textField();
        JButton create = AppTheme.primaryButton("Register");
        JButton back = AppTheme.secondaryButton("Back to Login");

        create.addActionListener(e -> {
            String u = username.getText().trim();
            String p = new String(password.getPassword()).trim();
            String l = limit.getText().trim();
            if (u.isEmpty() || p.isEmpty() || l.isEmpty()) {
                UIHelper.error(this, "Please fill all fields.");
                return;
            }
            if (AppData.findUserByUsername(u) != null) {
                UIHelper.error(this, "User already exists.");
                return;
            }
            try {
                double spendingLimit = Double.parseDouble(l);
                int userId = AppData.users.size() + 1;
                User user = new User(userId, u, p, spendingLimit);
                AppData.users.add(user);
                AppData.saveAll();
                UIHelper.info(this, "Registration successful. Please login.");
                frame.showLogin();
            } catch (Exception ex) {
                UIHelper.error(this, "Enter valid spending limit.");
            }
        });
        back.addActionListener(e -> frame.showLogin());

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(24));
        card.add(AppTheme.label("Username"));
        card.add(Box.createVerticalStrut(8));
        card.add(username);
        card.add(Box.createVerticalStrut(16));
        card.add(AppTheme.label("Password"));
        card.add(Box.createVerticalStrut(8));
        card.add(password);
        card.add(Box.createVerticalStrut(16));
        card.add(AppTheme.label("Monthly Spending Limit"));
        card.add(Box.createVerticalStrut(8));
        card.add(limit);
        card.add(Box.createVerticalStrut(24));
        card.add(create);
        card.add(Box.createVerticalStrut(12));
        card.add(back);
        add(card);
    }
}

// Main dashboard shown after login
class DashboardFrame extends JFrame {
    private final CardLayout pageLayout = new CardLayout();
    private final JPanel pagePanel = new JPanel(pageLayout);
    private final java.util.LinkedHashMap<String, JPanel> pages = new java.util.LinkedHashMap<>();
    private final java.util.LinkedHashMap<String, JButton> navButtons = new java.util.LinkedHashMap<>();
    private JLabel headerTitle;
    private JLabel headerSubtitle;

    DashboardFrame() {
        setTitle("Expense Manager Dashboard - " + AppData.currentUser.username);
        setSize(1420, 860);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(buildUI());
        refreshAll();
        showPage("Overview");
    }

    private JPanel buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BG);

        JPanel sidebar = buildSidebar();
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setOpaque(false);

        JPanel top = buildHeader();

        pagePanel.setOpaque(false);
        pagePanel.setBorder(AppTheme.pad(0, 0, 20, 20));

        pages.put("Overview", new OverviewPanel(this));
        pages.put("Groups", new GroupPanel(this));
        pages.put("Expenses", new ExpensePanel(this));
        pages.put("Balances", new BalancePanel(this));
        pages.put("Budget", new BudgetPanel(this));
        pages.put("Reports", new ReportPanel(this));

        for (String key : pages.keySet()) {
            pagePanel.add(pages.get(key), key);
        }

        mainArea.add(top, BorderLayout.NORTH);
        mainArea.add(pagePanel, BorderLayout.CENTER);

        root.add(sidebar, BorderLayout.WEST);
        root.add(mainArea, BorderLayout.CENTER);
        return root;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(10, 15, 28));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BorderLayout());
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(51, 65, 85)));

        JPanel brandWrap = new JPanel();
        brandWrap.setOpaque(false);
        brandWrap.setLayout(new BoxLayout(brandWrap, BoxLayout.Y_AXIS));
        brandWrap.setBorder(AppTheme.pad(26, 22, 18, 22));

        JLabel brand = new JLabel("Expense Manager");
        brand.setForeground(AppTheme.TEXT);
        brand.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JLabel tag = new JLabel("Java Swing expense tracker");
        tag.setForeground(AppTheme.MUTED);
        tag.setFont(AppTheme.small());
        brandWrap.add(brand);
        brandWrap.add(Box.createVerticalStrut(6));
        brandWrap.add(tag);

        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(AppTheme.pad(10, 14, 14, 14));

        String[] items = {"Overview", "Groups", "Expenses", "Balances", "Budget", "Reports"};
        for (String item : items) {
            JButton btn = createNavButton(item);
            navButtons.put(item, btn);
            nav.add(btn);
            nav.add(Box.createVerticalStrut(10));
        }

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setBorder(AppTheme.pad(10, 14, 22, 14));

        JButton refresh = AppTheme.secondaryButton("Refresh");
        refresh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refresh.addActionListener(e -> refreshAll());
        JButton logout = AppTheme.dangerButton("Logout");
        logout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logout.addActionListener(e -> {
            AppData.currentUser = null;
            AppData.saveAll();
            new AuthFrame().setVisible(true);
            dispose();
        });
        bottom.add(refresh);
        bottom.add(Box.createVerticalStrut(10));
        bottom.add(logout);

        sidebar.add(brandWrap, BorderLayout.NORTH);
        sidebar.add(nav, BorderLayout.CENTER);
        sidebar.add(bottom, BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setForeground(AppTheme.TEXT);
        btn.setBackground(new Color(15, 23, 42));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(
                new LineBorder(new Color(30, 41, 59), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        btn.addActionListener(e -> showPage(text));
        return btn;
    }

    private JPanel buildHeader() {
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(AppTheme.pad(20, 24, 16, 20));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        headerTitle = new JLabel("Welcome, " + AppData.currentUser.username);
        headerTitle.setForeground(AppTheme.TEXT);
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        headerSubtitle = new JLabel("Here is a quick summary of your expense records");
        headerSubtitle.setForeground(AppTheme.MUTED);
        headerSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        left.add(headerTitle);
        left.add(Box.createVerticalStrut(6));
        left.add(headerSubtitle);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        right.add(pill("Active User", AppTheme.SUCCESS));
        right.add(pill(AppData.currentUser.username, AppTheme.ACCENT));

        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    private JPanel pill(String text, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        p.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
        p.setBorder(new CompoundBorder(new LineBorder(color, 1, true), BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        JLabel l = new JLabel(text);
        l.setForeground(AppTheme.TEXT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        p.add(l);
        return p;
    }

    void showPage(String page) {
        pageLayout.show(pagePanel, page);
        for (String key : navButtons.keySet()) {
            JButton b = navButtons.get(key);
            boolean active = key.equals(page);
            b.setBackground(active ? AppTheme.ACCENT : new Color(15, 23, 42));
            b.setBorder(new CompoundBorder(
                    new LineBorder(active ? AppTheme.ACCENT_2 : new Color(30, 41, 59), 1, true),
                    BorderFactory.createEmptyBorder(14, 16, 14, 16)
            ));
        }
        headerSubtitle.setText(page + " section");
    }

    void refreshAll() {
        for (JPanel c : pages.values()) {
            if (c instanceof Refreshable) {
                ((Refreshable) c).refresh();
            }
        }
        setTitle("Expense Manager Dashboard - " + AppData.currentUser.username);
        revalidate();
        repaint();
    }
}

// Common interface so every page can refresh its data
interface Refreshable {
    void refresh();
}

// Home page which shows quick summary of user data
class OverviewPanel extends JPanel implements Refreshable {
    private final DashboardFrame parent;
    private final JPanel statsGrid = new JPanel(new GridLayout(1, 4, 16, 16));
    private final JTextArea recent = new JTextArea();

    OverviewPanel(DashboardFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(16, 16));
        setBackground(AppTheme.BG);
        setBorder(AppTheme.pad(18, 18, 18, 18));

        statsGrid.setOpaque(false);
        add(statsGrid, BorderLayout.NORTH);

        recent.setEditable(false);
        recent.setFont(AppTheme.body());
        recent.setLineWrap(true);
        recent.setWrapStyleWord(true);
        recent.setBackground(Color.WHITE);
        recent.setForeground(Color.BLACK);
        add(AppTheme.styledScroll(recent), BorderLayout.CENTER);
    }

    public void refresh() {
        statsGrid.removeAll();
        int myGroups = UIHelper.myGroups().size();
        int myPersonal = 0;
        int myRecurring = 0;
        double thisMonth = 0;
        String month = LocalDate.now().toString().substring(0, 7);
        for (Expense e : AppData.expenses) {
            if (e.groupId == 0 && e.ownerUsername.equalsIgnoreCase(AppData.currentUser.username)) {
                myPersonal++;
                if (e.date.startsWith(month)) thisMonth += e.totalAmount;
            }
        }
        for (RecurringExpense r : AppData.recurringExpenses) {
            if (r.ownerUsername.equalsIgnoreCase(AppData.currentUser.username)) myRecurring++;
        }
        statsGrid.add(statCard("My Groups", String.valueOf(myGroups), AppTheme.ACCENT));
        statsGrid.add(statCard("Personal Expenses", String.valueOf(myPersonal), AppTheme.ACCENT_2));
        statsGrid.add(statCard("Recurring", String.valueOf(myRecurring), AppTheme.SUCCESS));
        statsGrid.add(statCard("This Month", "Rs. " + String.format("%.2f", thisMonth), AppTheme.WARN));

        StringBuilder sb = new StringBuilder();
        sb.append("Recent activity for ").append(AppData.currentUser.username).append("\n\n");
        int count = 0;
        for (int i = AppData.expenses.size() - 1; i >= 0 && count < 8; i--) {
            Expense e = AppData.expenses.get(i);
            boolean visible = (e.groupId == 0 && e.ownerUsername.equalsIgnoreCase(AppData.currentUser.username));
            if (!visible && e.groupId != 0) {
                Group g = AppData.findGroupById(e.groupId);
                visible = g != null && g.members.contains(AppData.currentUser.username);
            }
            if (visible) {
                sb.append("• ").append(e.title).append(" | Rs. ").append(String.format("%.2f", e.totalAmount))
                  .append(" | ").append(e.category).append(" | ").append(e.date).append("\n");
                count++;
            }
        }
        if (count == 0) sb.append("No expenses yet. Start by adding a personal or group expense.");
        recent.setText(sb.toString());
    }

    private JPanel statCard(String title, String value, Color color) {
        JPanel p = AppTheme.card();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(color.darker());
        JLabel t = new JLabel(title);
        t.setForeground(AppTheme.TEXT);
        t.setFont(AppTheme.body());
        JLabel v = new JLabel(value);
        v.setForeground(Color.WHITE);
        v.setFont(new Font("Segoe UI", Font.BOLD, 24));
        p.add(t);
        p.add(Box.createVerticalStrut(10));
        p.add(v);
        return p;
    }
}

// Panel for creating groups and adding members
class GroupPanel extends JPanel implements Refreshable {
    private final DashboardFrame parent;
    private final JTable table = AppTheme.table(new String[]{"ID", "Group Name", "Admin", "Members"}, new Object[][]{});

    GroupPanel(DashboardFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(16, 16));
        setBackground(AppTheme.BG);
        setBorder(AppTheme.pad(18, 18, 18, 18));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        JButton create = AppTheme.primaryButton("Create Group");
        JButton addMembers = AppTheme.secondaryButton("Add Members");
        JButton delete = AppTheme.dangerButton("Delete Group");
        create.addActionListener(e -> createGroup());
        addMembers.addActionListener(e -> addMembers());
        delete.addActionListener(e -> deleteGroup());
        actions.add(create);
        actions.add(addMembers);
        actions.add(delete);

        add(actions, BorderLayout.NORTH);
        add(AppTheme.styledScroll(table), BorderLayout.CENTER);
    }

    public void refresh() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (Group g : UIHelper.myGroups()) {
            model.addRow(new Object[]{g.groupId, g.groupName, g.adminUsername, String.join(", ", g.members)});
        }
    }

    private void createGroup() {
        JTextField groupName = AppTheme.textField();
        JTextArea membersArea = new JTextArea(6, 25);
        membersArea.setText("Enter one username per line");
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{
                "Group Name", groupName,
                "Members (one username per line, excluding yourself)", new JScrollPane(membersArea)
        }, "Create Group", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;
        String name = groupName.getText().trim();
        if (name.isEmpty()) {
            UIHelper.error(this, "Group name cannot be empty.");
            return;
        }
        ArrayList<String> members = new ArrayList<>();
        members.add(AppData.currentUser.username);
        String[] lines = membersArea.getText().split("\\R");
        for (String line : lines) {
            String uname = line.trim();
            if (uname.isEmpty() || uname.equalsIgnoreCase("Enter one username per line")) continue;
            User u = AppData.findUserByUsername(uname);
            if (u != null && !members.contains(u.username)) members.add(u.username);
        }
        Group g = new Group(AppData.groups.size() + 1, name, AppData.currentUser.username, members);
        AppData.groups.add(g);
        AppData.saveAll();
        UIHelper.info(this, "Group created successfully. Group ID = " + g.groupId);
        parent.refreshAll();
    }

    private void addMembers() {
        java.util.List<Group> groups = UIHelper.myGroups();
        if (groups.isEmpty()) {
            UIHelper.error(this, "No groups found.");
            return;
        }
        JComboBox<String> box = new JComboBox<>(UIHelper.groupNames(groups));
        JTextArea membersArea = new JTextArea(6, 25);
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{
                "Select Group", box,
                "Add usernames (one per line)", new JScrollPane(membersArea)
        }, "Add Members", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;
        Group g = groups.get(box.getSelectedIndex());
        if (!g.adminUsername.equalsIgnoreCase(AppData.currentUser.username)) {
            UIHelper.error(this, "Only admin can add members.");
            return;
        }
        StringBuilder added = new StringBuilder();
        for (String line : membersArea.getText().split("\\R")) {
            String uname = line.trim();
            if (uname.isEmpty()) continue;
            User u = AppData.findUserByUsername(uname);
            if (u == null) continue;
            if (!g.members.contains(u.username)) {
                g.members.add(u.username);
                if (added.length() > 0) added.append(", ");
                added.append(u.username);
            }
        }
        AppData.saveAll();
        UIHelper.info(this, added.length() == 0 ? "No new members added." : "Added: " + added);
        parent.refreshAll();
    }

    private void deleteGroup() {
        java.util.List<Group> groups = UIHelper.myGroups();
        if (groups.isEmpty()) {
            UIHelper.error(this, "No groups available.");
            return;
        }
        JComboBox<String> box = new JComboBox<>(UIHelper.groupNames(groups));
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Select Group", box}, "Delete Group", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        Group g = groups.get(box.getSelectedIndex());
        if (!g.adminUsername.equalsIgnoreCase(AppData.currentUser.username)) {
            UIHelper.error(this, "Only admin can delete group.");
            return;
        }
        if (!UIHelper.confirm(this, "Delete group " + g.groupName + "?")) return;
        int gid = g.groupId;
        AppData.groups.remove(g);
        AppData.expenses.removeIf(e -> e.groupId == gid);
        AppData.settlements.removeIf(s -> s.groupId == gid);
        AppData.saveAll();
        parent.refreshAll();
    }
}

// Panel for personal, group and recurring expenses
class ExpensePanel extends JPanel implements Refreshable {
    private final DashboardFrame parent;
    private final JTable table = AppTheme.table(new String[]{"ID", "Type", "Title", "Amount", "Paid By", "Category", "Date", "Group", "Split"}, new Object[][]{});

    ExpensePanel(DashboardFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(16, 16));
        setBackground(AppTheme.BG);
        setBorder(AppTheme.pad(18, 18, 18, 18));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        JButton addPersonal = AppTheme.primaryButton("Add Personal Expense");
        JButton addGroup = AppTheme.secondaryButton("Add Group Expense");
        JButton search = AppTheme.secondaryButton("Search Expense");
        JButton recurring = AppTheme.secondaryButton("Add Recurring Expense");
        JButton viewRecurring = AppTheme.secondaryButton("View Recurring");
        addPersonal.addActionListener(e -> addPersonalExpense());
        addGroup.addActionListener(e -> addGroupExpense());
        search.addActionListener(e -> searchExpense());
        recurring.addActionListener(e -> addRecurringExpense());
        viewRecurring.addActionListener(e -> viewRecurringExpenses());
        actions.add(addPersonal);
        actions.add(addGroup);
        actions.add(search);
        actions.add(recurring);
        actions.add(viewRecurring);

        add(actions, BorderLayout.NORTH);
        add(AppTheme.styledScroll(table), BorderLayout.CENTER);
    }

    public void refresh() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for (Expense e : AppData.expenses) {
            boolean visible = false;
            String groupName = "-";
            if (e.groupId == 0 && e.ownerUsername.equalsIgnoreCase(AppData.currentUser.username)) {
                visible = true;
            } else if (e.groupId != 0) {
                Group g = AppData.findGroupById(e.groupId);
                if (g != null && g.members.contains(AppData.currentUser.username)) {
                    visible = true;
                    groupName = g.groupName;
                }
            }
            if (visible) {
                model.addRow(new Object[]{e.expenseId, e.splitType.equals("PERSONAL") ? "Personal" : "Group", e.title,
                        String.format("%.2f", e.totalAmount), e.paidBy, e.category, e.date, groupName, UIHelper.splitsAsText(e)});
            }
        }
    }

    private void addPersonalExpense() {
        JTextField title = AppTheme.textField();
        JTextField amount = AppTheme.textField();
        JComboBox<String> category = new JComboBox<>(ExpenseService.CATEGORIES);
        JTextField date = AppTheme.textField();
        JTextField note = AppTheme.textField();
        date.setText(UIHelper.today());

        int ok = JOptionPane.showConfirmDialog(this, new Object[]{
                "Title", title,
                "Amount", amount,
                "Category", category,
                "Date (yyyy-mm-dd)", date,
                "Note", note
        }, "Add Personal Expense", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;
        try {
            double amt = Double.parseDouble(amount.getText().trim());
            ArrayList<Split> splits = new ArrayList<>();
            splits.add(new Split(AppData.currentUser.username, amt));
            Expense e = new Expense(AppData.expenses.size() + 1, 0, AppData.currentUser.username,
                    title.getText().trim(), AppData.currentUser.username, amt, "PERSONAL",
                    (String) category.getSelectedItem(), date.getText().trim(), note.getText().trim(), splits);
            AppData.expenses.add(e);
            AppData.saveAll();
            BudgetService.checkBudgetWarning(AppData.currentUser.username, (String) category.getSelectedItem(), amt, date.getText().trim());
            UIHelper.info(this, "Personal expense added successfully.");
            parent.refreshAll();
        } catch (Exception ex) {
            UIHelper.error(this, "Enter valid amount.");
        }
    }

    private void addGroupExpense() {
        java.util.List<Group> groups = UIHelper.myGroups();
        if (groups.isEmpty()) {
            UIHelper.error(this, "No groups available.");
            return;
        }
        JComboBox<String> groupBox = new JComboBox<>(UIHelper.groupNames(groups));
        JTextField title = AppTheme.textField();
        JTextField amount = AppTheme.textField();
        JComboBox<String> category = new JComboBox<>(ExpenseService.CATEGORIES);
        JTextField date = AppTheme.textField();
        JTextField note = AppTheme.textField();
        JComboBox<String> splitType = new JComboBox<>(new String[]{"EQUAL", "EXACT", "PERCENTAGE"});
        date.setText(UIHelper.today());

        int ok = JOptionPane.showConfirmDialog(this, new Object[]{
                "Group", groupBox,
                "Title", title,
                "Amount", amount,
                "Category", category,
                "Date (yyyy-mm-dd)", date,
                "Note", note,
                "Split Type", splitType
        }, "Add Group Expense", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;
        Group g = groups.get(groupBox.getSelectedIndex());
        try {
            double total = Double.parseDouble(amount.getText().trim());
            String paidBy = (String) JOptionPane.showInputDialog(this, "Paid by", "Paid By", JOptionPane.PLAIN_MESSAGE, null,
                    g.members.toArray(new String[0]), g.members.get(0));
            if (paidBy == null || !g.members.contains(paidBy)) return;
            String type = (String) splitType.getSelectedItem();
            ArrayList<Split> splits;
            if ("EQUAL".equals(type)) {
                splits = SplitService.equalSplit(g.members, total);
            } else {
                splits = collectAdvancedSplits(g.members, total, "PERCENTAGE".equals(type));
                if (splits == null) return;
            }
            Expense e = new Expense(AppData.expenses.size() + 1, g.groupId, "", title.getText().trim(), paidBy,
                    total, type, (String) category.getSelectedItem(), date.getText().trim(), note.getText().trim(), splits);
            AppData.expenses.add(e);
            AppData.saveAll();
            UIHelper.info(this, "Group expense added successfully.");
            parent.refreshAll();
        } catch (Exception ex) {
            UIHelper.error(this, "Enter valid amount.");
        }
    }

    private ArrayList<Split> collectAdvancedSplits(java.util.List<String> members, double totalAmount, boolean percentage) {
        JPanel panel = new JPanel(new GridLayout(members.size(), 2, 8, 8));
        panel.setBackground(Color.WHITE);
        java.util.List<JTextField> fields = new ArrayList<>();
        for (String m : members) {
            panel.add(new JLabel((percentage ? "Percentage for " : "Amount for ") + m));
            JTextField tf = new JTextField();
            fields.add(tf);
            panel.add(tf);
        }
        int ok = JOptionPane.showConfirmDialog(this, panel, percentage ? "Enter Percentages" : "Enter Exact Amounts",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return null;
        ArrayList<Split> list = new ArrayList<>();
        try {
            double sum = 0;
            for (int i = 0; i < members.size(); i++) {
                double val = Double.parseDouble(fields.get(i).getText().trim());
                sum += val;
                list.add(new Split(members.get(i), percentage ? totalAmount * val / 100.0 : val));
            }
            if (percentage && Math.abs(sum - 100.0) > 0.01) {
                UIHelper.error(this, "Percentages must sum to 100.");
                return null;
            }
            if (!percentage && Math.abs(sum - totalAmount) > 0.01) {
                UIHelper.error(this, "Exact split total does not match expense total.");
                return null;
            }
            return list;
        } catch (Exception ex) {
            UIHelper.error(this, "Please enter valid numbers.");
            return null;
        }
    }

    private void searchExpense() {
        String key = JOptionPane.showInputDialog(this, "Enter title or category to search:");
        if (key == null || key.trim().isEmpty()) return;
        key = key.trim().toLowerCase();
        StringBuilder sb = new StringBuilder();
        for (Expense e : AppData.expenses) {
            boolean visible = false;
            if (e.groupId == 0 && e.ownerUsername.equalsIgnoreCase(AppData.currentUser.username)) visible = true;
            else if (e.groupId != 0) {
                Group g = AppData.findGroupById(e.groupId);
                visible = g != null && g.members.contains(AppData.currentUser.username);
            }
            if (visible && (e.title.toLowerCase().contains(key) || e.category.toLowerCase().contains(key))) {
                sb.append("ID ").append(e.expenseId).append(" | ").append(e.title).append(" | Rs. ")
                        .append(String.format("%.2f", e.totalAmount)).append(" | ").append(e.category)
                        .append(" | ").append(e.date).append("\n");
            }
        }
        UIHelper.info(this, sb.length() == 0 ? "No matching expense found." : sb.toString());
    }

    private void addRecurringExpense() {
        JTextField title = AppTheme.textField();
        JTextField amount = AppTheme.textField();
        JComboBox<String> category = new JComboBox<>(ExpenseService.CATEGORIES);
        JComboBox<String> frequency = new JComboBox<>(new String[]{"MONTHLY", "WEEKLY"});
        JTextField nextDate = AppTheme.textField();
        JTextField note = AppTheme.textField();
        nextDate.setText(UIHelper.today());
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{
                "Title", title,
                "Amount", amount,
                "Category", category,
                "Frequency", frequency,
                "Next Due Date", nextDate,
                "Note", note
        }, "Add Recurring Expense", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;
        try {
            double amt = Double.parseDouble(amount.getText().trim());
            RecurringExpense r = new RecurringExpense(AppData.recurringExpenses.size() + 1,
                    AppData.currentUser.username, title.getText().trim(), amt,
                    (String) category.getSelectedItem(), (String) frequency.getSelectedItem(),
                    nextDate.getText().trim(), note.getText().trim());
            AppData.recurringExpenses.add(r);
            AppData.saveAll();
            UIHelper.info(this, "Recurring expense added successfully.");
            parent.refreshAll();
        } catch (Exception ex) {
            UIHelper.error(this, "Enter valid amount.");
        }
    }

    private void viewRecurringExpenses() {
        StringBuilder sb = new StringBuilder();
        for (RecurringExpense r : AppData.recurringExpenses) {
            if (r.ownerUsername.equalsIgnoreCase(AppData.currentUser.username)) {
                sb.append("ID: ").append(r.recurringId)
                        .append(" | ").append(r.title)
                        .append(" | Rs. ").append(String.format("%.2f", r.amount))
                        .append(" | ").append(r.category)
                        .append(" | ").append(r.frequency)
                        .append(" | Next: ").append(r.nextDueDate)
                        .append("\n");
            }
        }
        UIHelper.info(this, sb.length() == 0 ? "No recurring expenses found." : sb.toString());
    }
}

// Panel for balances, settlement and who owes whom
class BalancePanel extends JPanel implements Refreshable {
    private final DashboardFrame parent;
    private final JTextArea output = new JTextArea();

    BalancePanel(DashboardFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(16, 16));
        setBackground(AppTheme.BG);
        setBorder(AppTheme.pad(18, 18, 18, 18));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        JButton balances = AppTheme.primaryButton("Show Group Balances");
        JButton owes = AppTheme.secondaryButton("Show Who Owes Whom");
        JButton settle = AppTheme.secondaryButton("Settle Up");
        JButton history = AppTheme.secondaryButton("Settlement History");
        balances.addActionListener(e -> showBalances());
        owes.addActionListener(e -> showWhoOwesWhom());
        settle.addActionListener(e -> settleUp());
        history.addActionListener(e -> showSettlementHistory());
        actions.add(balances);
        actions.add(owes);
        actions.add(settle);
        actions.add(history);

        output.setEditable(false);
        output.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        output.setBackground(Color.WHITE);
        output.setForeground(Color.BLACK);
        add(actions, BorderLayout.NORTH);
        add(AppTheme.styledScroll(output), BorderLayout.CENTER);
    }

    public void refresh() {
        output.setText("Choose an action above to view balances, dues, and settlement history.");
    }

    private Group chooseGroup() {
        java.util.List<Group> groups = UIHelper.myGroups();
        if (groups.isEmpty()) {
            UIHelper.error(this, "No groups found.");
            return null;
        }
        JComboBox<String> box = new JComboBox<>(UIHelper.groupNames(groups));
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Select Group", box}, "Select Group", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return null;
        return groups.get(box.getSelectedIndex());
    }

    private void showBalances() {
        Group g = chooseGroup();
        if (g == null) return;
        HashMap<String, Double> net = BalanceService.calculateNetBalancesForGroup(g.groupId);
        StringBuilder sb = new StringBuilder("GROUP BALANCES - " + g.groupName + "\n\n");
        for (String user : net.keySet()) {
            double value = net.get(user);
            if (value > 0.01) sb.append(user).append(" should receive Rs. ").append(String.format("%.2f", value)).append("\n");
            else if (value < -0.01) sb.append(user).append(" should pay Rs. ").append(String.format("%.2f", -value)).append("\n");
            else sb.append(user).append(" is settled.\n");
        }
        output.setText(sb.toString());
    }

    private void showWhoOwesWhom() {
        Group g = chooseGroup();
        if (g == null) return;
        HashMap<String, Double> net = BalanceService.calculateNetBalancesForGroup(g.groupId);
        ArrayList<String> debtors = new ArrayList<>();
        ArrayList<Double> debtorAmt = new ArrayList<>();
        ArrayList<String> creditors = new ArrayList<>();
        ArrayList<Double> creditorAmt = new ArrayList<>();
        for (String user : net.keySet()) {
            double amt = net.get(user);
            if (amt < -0.01) {
                debtors.add(user);
                debtorAmt.add(-amt);
            } else if (amt > 0.01) {
                creditors.add(user);
                creditorAmt.add(amt);
            }
        }
        StringBuilder sb = new StringBuilder("WHO OWES WHOM - " + g.groupName + "\n\n");
        if (debtors.isEmpty() && creditors.isEmpty()) {
            sb.append("All settled.");
        } else {
            int i = 0, j = 0;
            while (i < debtors.size() && j < creditors.size()) {
                double x = Math.min(debtorAmt.get(i), creditorAmt.get(j));
                sb.append(debtors.get(i)).append(" owes ").append(creditors.get(j))
                        .append(" Rs. ").append(String.format("%.2f", x)).append("\n");
                debtorAmt.set(i, debtorAmt.get(i) - x);
                creditorAmt.set(j, creditorAmt.get(j) - x);
                if (debtorAmt.get(i) < 0.01) i++;
                if (creditorAmt.get(j) < 0.01) j++;
            }
        }
        output.setText(sb.toString());
    }

    private void settleUp() {
        Group g = chooseGroup();
        if (g == null) return;
        JComboBox<String> from = new JComboBox<>(g.members.toArray(new String[0]));
        JComboBox<String> to = new JComboBox<>(g.members.toArray(new String[0]));
        JTextField amount = AppTheme.textField();
        JTextField date = AppTheme.textField();
        JComboBox<String> mode = new JComboBox<>(new String[]{"Cash", "UPI", "Bank"});
        date.setText(UIHelper.today());
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{
                "From User", from,
                "To User", to,
                "Amount", amount,
                "Date", date,
                "Mode", mode
        }, "Settle Up", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        try {
            double amt = Double.parseDouble(amount.getText().trim());
            Settlement s = new Settlement(AppData.settlements.size() + 1, g.groupId,
                    (String) from.getSelectedItem(), (String) to.getSelectedItem(), amt,
                    date.getText().trim(), (String) mode.getSelectedItem());
            AppData.settlements.add(s);
            AppData.saveAll();
            UIHelper.info(this, "Settlement added successfully.");
            parent.refreshAll();
        } catch (Exception ex) {
            UIHelper.error(this, "Enter valid amount.");
        }
    }

    private void showSettlementHistory() {
        String[] opts = {"My Settlement History", "Group Settlement History"};
        int choice = JOptionPane.showOptionDialog(this, "Choose history type", "Settlement History", 0,
                JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
        StringBuilder sb = new StringBuilder();
        if (choice == 0) {
            sb.append("MY SETTLEMENT HISTORY\n\n");
            for (Settlement s : AppData.settlements) {
                if (s.fromUser.equalsIgnoreCase(AppData.currentUser.username) || s.toUser.equalsIgnoreCase(AppData.currentUser.username)) {
                    sb.append(UIHelper.settlementSummary(s)).append("\n");
                }
            }
        } else if (choice == 1) {
            Group g = chooseGroup();
            if (g == null) return;
            sb.append("GROUP SETTLEMENT HISTORY - ").append(g.groupName).append("\n\n");
            for (Settlement s : AppData.settlements) {
                if (s.groupId == g.groupId) sb.append(UIHelper.settlementSummary(s)).append("\n");
            }
        } else return;
        output.setText(sb.length() == 0 ? "No settlement history found." : sb.toString());
    }
}

// Panel for category wise budget and monthly spending limit
class BudgetPanel extends JPanel implements Refreshable {
    private final DashboardFrame parent;
    private final JTable table = AppTheme.table(new String[]{"Category", "Budget", "Spent (This Month)", "Remaining", "Status"}, new Object[][]{});
    private final JLabel overall = new JLabel();

    BudgetPanel(DashboardFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(16, 16));
        setBackground(AppTheme.BG);
        setBorder(AppTheme.pad(18, 18, 18, 18));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        JButton setBudget = AppTheme.primaryButton("Set Category Budget");
        JButton overallLimit = AppTheme.secondaryButton("Update Overall Limit");
        setBudget.addActionListener(e -> setCategoryBudget());
        overallLimit.addActionListener(e -> updateOverallLimit());
        actions.add(setBudget);
        actions.add(overallLimit);
        overall.setForeground(AppTheme.TEXT);
        overall.setFont(AppTheme.h2());
        top.add(actions, BorderLayout.WEST);
        top.add(overall, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(AppTheme.styledScroll(table), BorderLayout.CENTER);
    }

    public void refresh() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        String month = LocalDate.now().toString().substring(0, 7);
        for (String category : AppData.currentUser.categoryBudgets.keySet()) {
            double budget = AppData.currentUser.categoryBudgets.get(category);
            double spent = BudgetService.getMonthlySpentForCategory(AppData.currentUser.username, category, month);
            double remaining = budget - spent;
            String status = (budget > 0 && spent > budget) ? "Exceeded" : "Within limit";
            model.addRow(new Object[]{category, String.format("%.2f", budget), String.format("%.2f", spent),
                    String.format("%.2f", remaining), status});
        }
        double totalSpent = BudgetService.getTotalMonthlyPersonalSpent(AppData.currentUser.username, month);
        overall.setText("Overall Limit: Rs. " + String.format("%.2f", AppData.currentUser.spendingLimit) +
                "   |   This Month: Rs. " + String.format("%.2f", totalSpent));
    }

    private void setCategoryBudget() {
        java.util.List<String> categories = new ArrayList<>(AppData.currentUser.categoryBudgets.keySet());
        JComboBox<String> category = new JComboBox<>(categories.toArray(new String[0]));
        JTextField amount = AppTheme.textField();
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Category", category, "Budget Amount", amount},
                "Set Category Budget", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        try {
            double amt = Double.parseDouble(amount.getText().trim());
            AppData.currentUser.categoryBudgets.put((String) category.getSelectedItem(), amt);
            AppData.saveAll();
            parent.refreshAll();
        } catch (Exception ex) {
            UIHelper.error(this, "Enter valid amount.");
        }
    }

    private void updateOverallLimit() {
        JTextField amount = AppTheme.textField();
        amount.setText(String.valueOf(AppData.currentUser.spendingLimit));
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Overall Monthly Spending Limit", amount},
                "Update Overall Limit", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        try {
            AppData.currentUser.spendingLimit = Double.parseDouble(amount.getText().trim());
            AppData.saveAll();
            parent.refreshAll();
        } catch (Exception ex) {
            UIHelper.error(this, "Enter valid amount.");
        }
    }
}

// Panel for text report and graph report
class ReportPanel extends JPanel implements Refreshable {
    private final DashboardFrame parent;
    private final JTextArea output = new JTextArea();
    private JPanel graphArea = new JPanel(new BorderLayout());

    ReportPanel(DashboardFrame parent) {
        this.parent = parent;
        setLayout(new BorderLayout(16, 16));
        setBackground(AppTheme.BG);
        setBorder(AppTheme.pad(18, 18, 18, 18));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);

        JButton summary = AppTheme.primaryButton("Show Monthly Summary");
        JButton export = AppTheme.secondaryButton("Export Report");
        JButton deleteUser = AppTheme.dangerButton("Delete Account");

        summary.addActionListener(e -> monthlySummary());
        export.addActionListener(e -> exportReport());
        deleteUser.addActionListener(e -> deleteUser());

        actions.add(summary);
        actions.add(export);
        actions.add(deleteUser);

        output.setEditable(false);
        output.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        output.setBackground(Color.WHITE);
        output.setForeground(Color.BLACK);

        graphArea.setBackground(Color.WHITE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBackground(Color.WHITE);
        tabs.setForeground(Color.BLACK);
        tabs.addTab("Text Report", AppTheme.styledScroll(output));
        tabs.addTab("Graph Report", graphArea);

        add(actions, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    public void refresh() {
        output.setText("Use this section to view monthly summary, export report, or delete account.");
    }

    private void monthlySummary() {
        String month = JOptionPane.showInputDialog(this, "Enter month prefix (yyyy-mm):", LocalDate.now().toString().substring(0, 7));
        if (month == null || month.trim().isEmpty()) return;

        double personalTotal = 0.0;
        HashMap<String, Double> categoryMap = new HashMap<>();

        for (Expense e : AppData.expenses) {
            if (e.groupId == 0 &&
                e.ownerUsername.equalsIgnoreCase(AppData.currentUser.username) &&
                e.date.startsWith(month)) {

                personalTotal += e.totalAmount;
                categoryMap.put(e.category, categoryMap.getOrDefault(e.category, 0.0) + e.totalAmount);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("MONTHLY SPENDING SUMMARY\n\n");
        sb.append("User: ").append(AppData.currentUser.username).append("\n");
        sb.append("Month: ").append(month).append("\n");
        sb.append("Total Personal Spending: Rs. ").append(String.format("%.2f", personalTotal)).append("\n\n");
        sb.append("Category-wise Summary\n");

        for (String cat : categoryMap.keySet()) {
            sb.append(cat).append(" -> Rs. ").append(String.format("%.2f", categoryMap.get(cat))).append("\n");
        }

        if (categoryMap.isEmpty()) {
            sb.append("No personal expenses found for this month.");
        }

        output.setText(sb.toString());

        graphArea.removeAll();
        graphArea.add(new BarChartPanel("Category-wise Spending Graph", categoryMap), BorderLayout.CENTER);
        graphArea.revalidate();
        graphArea.repaint();
    }

    private void exportReport() {
        UIHelper.info(this, "Export feature can be connected here using the old report export code.");
    }

    private void deleteUser() {
        if (!UIHelper.confirm(this, "Delete your account and related data?")) return;

        String username = AppData.currentUser.username;

        AppData.users.removeIf(u -> u.username.equalsIgnoreCase(username));
        AppData.recurringExpenses.removeIf(r -> r.ownerUsername.equalsIgnoreCase(username));
        AppData.expenses.removeIf(e -> e.ownerUsername.equalsIgnoreCase(username) || e.paidBy.equalsIgnoreCase(username));

        for (Group g : AppData.groups) {
            g.members.removeIf(m -> m.equalsIgnoreCase(username));
        }

        AppData.groups.removeIf(g -> g.adminUsername.equalsIgnoreCase(username) || g.members.isEmpty());
        AppData.settlements.removeIf(s -> s.fromUser.equalsIgnoreCase(username) || s.toUser.equalsIgnoreCase(username));

        AppData.saveAll();

        UIHelper.info(this, "User deleted successfully.");
        AppData.currentUser = null;
        SwingUtilities.getWindowAncestor(this).dispose();
        new AuthFrame().setVisible(true);
    }
}
// Simple bar chart panel made using Graphics2D
class BarChartPanel extends JPanel {
    private HashMap<String, Double> data;
    private String title;

    BarChartPanel(String title, HashMap<String, Double> data) {
        this.title = title;
        this.data = data;
        setBackground(Color.WHITE);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g2.setColor(Color.BLACK);
        g2.drawString(title, 30, 35);

        if (data == null || data.isEmpty()) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            g2.drawString("No data available for graph.", 30, 80);
            return;
        }

        double max = 0;
        for (double value : data.values()) {
            if (value > max) max = value;
        }

        int x = 70;
        int yBase = 330;
        int barWidth = 70;
        int gap = 40;
        int maxHeight = 220;

        for (String key : data.keySet()) {
            double value = data.get(key);
            int barHeight = (int) ((value / max) * maxHeight);

            g2.setColor(new Color(99, 102, 241));
            g2.fillRoundRect(x, yBase - barHeight, barWidth, barHeight, 12, 12);

            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.drawString("Rs." + String.format("%.0f", value), x, yBase - barHeight - 10);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.drawString(key, x, yBase + 20);

            x += barWidth + gap;
        }

        g2.setColor(Color.BLACK);
        g2.drawLine(45, yBase, 900, yBase);
        g2.drawLine(45, 80, 45, yBase);
    }
}