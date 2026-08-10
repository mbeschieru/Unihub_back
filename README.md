# Environment Setup

## Unix-like Systems (macOS/Linux)

1. Make the script executable:
```bash
chmod +x setup-env.sh
```

2. Run the script:
```bash
./setup-env.sh
```

3. To make the variables permanent, add them to your shell configuration file:
```bash
echo "source $(pwd)/setup-env.sh" >> ~/.zshrc  # for zsh
# or
echo "source $(pwd)/setup-env.sh" >> ~/.bashrc  # for bash
```

## Windows

1. Run the batch script:
```cmd
setup-env.bat
```

2. To make the variables permanent, add them to your system environment variables:
- Open System Properties > Advanced > Environment Variables
- Add the variables under "User variables"

## Required Variables to Fill

After running the setup script, you need to fill in the following variables:

1. Email Configuration:
   - `SPRING_MAIL_USERNAME`
   - `SPRING_MAIL_PASSWORD`

2. OAuth2 Configuration:
   - `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID`
   - `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET`
   - `GITHUB_CLIENT_ID`
   - `GITHUB_CLIENT_SECRET`

3. WebSocket Configuration:
   - `SPRING_WEBSOCKET_MESSAGE_BROKER_RELAY_LOGIN`
   - `SPRING_WEBSOCKET_MESSAGE_BROKER_RELAY_PASSCODE`

4. JWT Configuration:
   - `JWT_SECRET` (replace with a secure random string)

## Development vs Production

For development, you can use the default values in the scripts. For production:

1. Create a separate script with production values
2. Use environment-specific configuration files
3. Use a secrets management system
4. Never commit sensitive values to version control 