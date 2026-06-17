package com.example.data

import kotlinx.coroutines.flow.Flow

class AetheroRepository(private val dao: AppDao) {
    val allPlugins: Flow<List<PluginEntity>> = dao.getAllPlugins()
    val pluginCount: Flow<Int> = dao.getPluginCount()
    val commandCount: Flow<Int> = dao.getCommandCount()
    val recentActivity: Flow<List<ActivityEntity>> = dao.getRecentActivity()

    suspend fun getPlugin(id: String): PluginEntity? = dao.getPluginById(id)
    
    suspend fun updatePlugin(plugin: PluginEntity) {
        dao.updatePlugin(plugin)
    }

    suspend fun populateDummyDataIfNeeded() {
        // Initial setup for empty DB to match mockups
        val dummyPlugins = listOf(
            PluginEntity(
                id = "eval.js",
                name = "eval",
                isCommand = true,
                usePrefix = false,
                cases = ">, =>, \$",
                description = "Ejecuta código asíncrono (JavaScript) y comandos de consola (Shell).",
                category = "Owner",
                usages = "> <script>|=> <return script>|\$ <shell>",
                code = "const { exec } = require('child_process');\n\nmodule.exports = {\n  command: true,\n  usePrefix: false,\n  case: ['>', '=>', '$'],\n  description: 'Ejecuta código asíncrono.\\n(JavaScript) y comandos de consola.\\n(Shell).',\n  category: 'owner',\n  usage: ['> <script>', '=> <return script>', '$ <shell>'],\n};\n\nrun: async (client, message, args) => {\n  const text = args.join(' ');\n  if (!text) return message.reply('Ingresa un código o comando para ejecutar.');\n  \n  try {\n    if (message.body.startsWith('$')) {\n      exec(text, (err, stdout, stderr) => {\n        if (err) return message.reply(stderr);\n        message.reply(stdout || 'Comando ejecutado.');\n      });\n    } else {\n      let result = await eval(`(async () => { \\n\${text} \\n})()`);\n      if (result !== undefined) {\n        message.reply(require('util').inspect(result, { depth: 0 }));\n      }\n    }\n  } catch (error) {\n    message.reply(`Error: \${error}`);\n  }\n};",
                isActive = true,
                usesCount = 2400,
                version = "v1.3.0"
            ),
            PluginEntity(
                id = "ping.js",
                name = "ping",
                isCommand = true,
                usePrefix = true,
                cases = "ping",
                description = "Muestra la latencia del bot.",
                category = "Utility",
                usages = "!ping",
                code = "// ping code here",
                isActive = true,
                usesCount = 1800,
                version = "v1.2.1"
            ),
            PluginEntity(
                id = "menu.js",
                name = "menu",
                isCommand = true,
                usePrefix = true,
                cases = "menu, help",
                description = "Muestra un menú interactivo.",
                category = "General",
                usages = "!menu",
                code = "// menu code here",
                isActive = true,
                usesCount = 1200,
                version = "v2.0.0"
            ),
            PluginEntity(
                id = "broadcast.js",
                name = "broadcast",
                isCommand = true,
                usePrefix = true,
                cases = "bc, broadcast",
                description = "Envía mensajes a múltiples usuarios.",
                category = "Owner",
                usages = "!broadcast <message>",
                code = "// broadcast code here",
                isActive = false,
                usesCount = 980,
                version = "v1.1.0"
            ),
            PluginEntity(
                id = "welcome.js",
                name = "welcome",
                isCommand = false,
                usePrefix = false,
                cases = "",
                description = "Mensaje de bienvenida al nuevo usuario.",
                category = "Utility",
                usages = "",
                code = "// welcome code here",
                isActive = true,
                usesCount = 870,
                version = "v1.0.2"
            )
        )
        dao.insertPlugins(dummyPlugins)
        
        dao.insertActivity(ActivityEntity(actionType = "Sincronización", description = "Última sincronización completada.", date = System.currentTimeMillis()))
    }
}
