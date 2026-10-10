## 1.3.0 port2

- Preserved the user-supplied KubeJS fix: removed the declaration of the nonexistent plugin from the packaged resources. This prevents the plugin ClassNotFoundException; it does not add a KubeJS scripting integration.
- Added a Naquadah Reactor template for Mekanism Extras with the 9x9x9 shape, empty interior, controller, laser focus matrix and ports.
- Added English and Brazilian Portuguese template names.
- Verified server/client template loading and a real formed reactor. Configure an output port with the Configurator after construction.
