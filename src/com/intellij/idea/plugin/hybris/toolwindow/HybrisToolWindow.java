package com.intellij.idea.plugin.hybris.toolwindow;

import com.intellij.idea.plugin.hybris.common.services.CommonIdeaService;
import com.intellij.idea.plugin.hybris.common.utils.HybrisI18NBundleUtils;
import com.intellij.idea.plugin.hybris.notifications.NotificationUtil;
import com.intellij.idea.plugin.hybris.settings.HybrisProjectSettings;
import com.intellij.idea.plugin.hybris.settings.HybrisProjectSettingsComponent;
import com.intellij.idea.plugin.hybris.tools.remote.http.HybrisHacHttpClient;
import com.intellij.idea.plugin.hybris.toolwindow.document.filter.UnsignedIntegerDocumentFilter;
import com.intellij.idea.plugin.hybris.toolwindow.document.listener.SimpleDocumentListener;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.PlainDocument;

public class HybrisToolWindow implements ToolWindowFactory, DumbAware {
    private ToolWindow myToolWindow;
    private Project myProject;
    private JPanel myToolWindowContent;
    private JTextField projectIpTextField;
    private JLabel projectIpLabel;
    private JTextField loginTextField;
    private JPasswordField passwordField;
    private JButton testConnectionButton;
    private JLabel loginNameLabel;
    private JLabel passwordLabel;
    private JLabel projectPortLabel;
    private JTextField projectPortTextField;
    private JLabel projectUrlPreviewValueLabel;
    private JLabel projectUrlPreviewLabel;
    private JLabel hacWebrootLabel;
    private JTextField hacWebrootTextField;

    @Override
    public void createToolWindowContent(
        @NotNull final Project project, @NotNull final ToolWindow toolWindow
    ) {
        myToolWindow = toolWindow;
        myProject = project;
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindowContent, "", false);
        toolWindow.getContentManager().addContent(content);
        projectIpTextField.addActionListener(action->saveSettings());
        hacWebrootTextField.addActionListener(action->saveSettings());
        loginTextField.addActionListener(action->saveSettings());
        passwordField.addActionListener(action->saveSettings());
        testConnectionButton.addActionListener(action->testConnection());
        final HybrisProjectSettings settings = HybrisProjectSettingsComponent.getInstance(myProject).getState();
        projectIpTextField.setText(settings.getHostIP());
        projectPortTextField.setText(settings.getPort());
        ((PlainDocument)projectPortTextField.getDocument()).setDocumentFilter(new UnsignedIntegerDocumentFilter());
        hacWebrootTextField.setText(settings.getHacWebroot());
        loginTextField.setText(settings.getHacLogin());
        passwordField.setText(settings.getHacPassword());

        final SimpleDocumentListener generateUrlPreviewDocumentListener = new SimpleDocumentListener() {
            @Override
            public void update(final DocumentEvent e) {
                generateHacPreviewUrl();
            }
        };
        projectIpTextField.getDocument().addDocumentListener(generateUrlPreviewDocumentListener);
        projectPortTextField.getDocument().addDocumentListener(generateUrlPreviewDocumentListener);
        hacWebrootTextField.getDocument().addDocumentListener(generateUrlPreviewDocumentListener);

        generateHacPreviewUrl();
    }

    private void generateHacPreviewUrl() {
        saveSettings();
        final String previewUrl = CommonIdeaService.getInstance().getHostHacUrl(myProject);
        projectUrlPreviewValueLabel.setText(previewUrl);
    }

    private void testConnection() {
        saveSettings();

        HybrisHacHttpClient hybrisHacHttpClient = HybrisHacHttpClient.getInstance(myProject);
        final String errorMessage = hybrisHacHttpClient.login(myProject);
        final String testedHacURL = hybrisHacHttpClient.getHostHacURL(myProject);

        final NotificationType type;
        final String message;
        if (errorMessage.isEmpty()) {
            message = HybrisI18NBundleUtils.message("hybris.toolwindow.hac.test.connection.success", testedHacURL);
            type = NotificationType.INFORMATION;
        } else {
            type = NotificationType.WARNING;
            message = HybrisI18NBundleUtils.message("hybris.toolwindow.hac.test.connection.fail", testedHacURL, errorMessage);
        }

        NotificationUtil.NOTIFICATION_GROUP.createNotification(
            HybrisI18NBundleUtils.message("hybris.toolwindow.hac.test.connection.title"), message, type, null
        ).notify(myProject);
    }

    private void saveSettings() {
        final HybrisProjectSettings settings = HybrisProjectSettingsComponent.getInstance(myProject).getState();
        settings.setHostIP(projectIpTextField.getText());
        settings.setPort(projectPortTextField.getText());
        settings.setHacWebroot(hacWebrootTextField.getText());
        settings.setHacLogin(loginTextField.getText());
        settings.setHacPassword(new String(passwordField.getPassword()));
    }

    @Override
    public boolean shouldBeAvailable(@NotNull final Project project) {
        return HybrisProjectSettingsComponent.getInstance(project).getState().isHybrisProject();
    }
}
