package com.cpwr.gdo.simulator.web.security;

import org.apache.click.ActionListener;
import org.apache.click.Control;
import org.apache.click.control.Form;
import org.apache.click.control.HiddenField;
import org.apache.click.control.PasswordField;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.extras.control.EmailField;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.cpwr.gdo.simulator.model.security.User;
import com.cpwr.gdo.simulator.service.AuthDetailsService;
import com.cpwr.gdo.simulator.web.page.BorderPage;
import com.cpwr.gdo.simulator.web.security.secure.SecurePage;

/**
 * Provides an Spring Security (ACEGI) enabled account creation page.
 */
@Component
public class CreateAccount extends BorderPage {

    private static final long serialVersionUID = 1L;

    private static final String EMAIL_PROPERTY = "email";
    private static final String FULLNAME_PROPERTY = "fullname";
    private static final String PASSWORD_PROPERTY = "password";
    private static final String USERNAME_PROPERTY = "username";

    private Form form = new Form("form");
    private TextField fullNameField = new TextField(FULLNAME_PROPERTY, "Full Name", true);
    private EmailField emailField = new EmailField(EMAIL_PROPERTY);
    private TextField userNameField = new TextField(USERNAME_PROPERTY, true);
    private PasswordField passwordField = new PasswordField("password", true);
    private PasswordField passwordAgainField = new PasswordField("passwordAgain", "Password again", true);
    private HiddenField redirectField = new HiddenField("redirect", String.class);

    @Autowired
    @Qualifier(value = "authenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier(value = "authService")
    private AuthDetailsService userService;

    // Constructor ------------------------------------------------------------

    public CreateAccount() {
        addControl(form);

        form.setDefaultFieldSize(30);

        form.add(fullNameField);
        form.add(emailField);
        form.add(userNameField);
        form.add(passwordField);
        form.add(passwordAgainField);

        Submit submit = new Submit("create");
        submit.setActionListener(new ActionListener() {
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control source) {
                return onCreate();
            }
        });
        form.add(submit);

        form.add(redirectField);
    }

    // Event Handlers ---------------------------------------------------------

    @Override
    public void onInit() {
        super.onInit();

        if (getContext().isGet()) {
            redirectField.setValue(getContext().getRequestParameter("redirect"));
        }
    }

    @Override
    public String getHelpPageLink() {
        return "";
    }

    public boolean onCreate() {
        if (form.isValid()) {

            String fullName = fullNameField.getValue();
            String email = emailField.getValue();
            String username = userNameField.getValue();
            String password1 = passwordField.getValue();
            String password2 = passwordAgainField.getValue();

            if (!password1.equals(password2)) {
                passwordField.setError("Password and password again do not match");
                return true;
            }

            User user = null;

            try {
                user = (User) userService.loadUserByUsername(username);
            } catch (UsernameNotFoundException unfe) {

            }
            ;

            if (user != null) {
                userNameField.setError(getMessage("usernameExistsError"));
                return true;
            }

            // new user
            user = userService.createUser(fullName, email, username, password1);

            Authentication token = new UsernamePasswordAuthenticationToken(username, password1);
            Authentication result = authenticationManager.authenticate(token);
            SecurityContext securityContext = new SecurityContextImpl();
            securityContext.setAuthentication(result);
            SecurityContextHolder.setContext(securityContext);

            String path = redirectField.getValue();

            if (StringUtils.isNotBlank(path)) {
                setRedirect(path);
            } else {
                setRedirect(SecurePage.class);
            }
        }

        return true;
    }

}
