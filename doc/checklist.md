# Checklist para realizar un buen proyecto

Cosas que debes hacer:

- [ ] Sugerimos que te instales los módulos de VScode que vienen recomendados en este repositorio. Para ello, vete a módulos y en la opción de buscar pon `@recommended`. Instálate todas las "*Workspace recommendations*".

- [ ] Asegúrate de que en el fichero [`/doc/doc.md`](doc.md) figure un índice de apartados que faciliten la lectura del documento. **Es obligatorio que haya índice**. Instálate el complemento recomendado [Markdown All in One en Vscode](https://marketplace.visualstudio.com/items?itemName=yzhang.markdown-all-in-one) (o busca alternativas) para facilitarte la generación de este índice. En la paleta de comandos de VSCode están las opciones de esta extensión.

- [ ] Dispones de la extensión [Markdown PDF](https://marketplace.visualstudio.com/items?itemName=yzane.markdown-pdf) para exportar tu [`/doc/doc.md`](doc.md) a dicho formato. En la paleta de comandos de VSCode están las opciones de esta extensión. NO incluyas el PDF a tu repo (no es lugar para ello). Esta extensión está por defecto ignorada para que no se sincronice con gitlab. Si tu tutor te pide que consignes el proyecto en pdf, ya sabes como hacerlo.

- [ ] Importante **describir bien el proyecto**. Propósito, partes, funcionalidad, posibles tipos de usuarios, roles, etc.

- [ ] Tanto en el [`README.md`](../README.md) como en [`doc/doc.md`](doc.md) tienes una serie de *TODOs* y ayudas contextuales en forma de citas. Asegúrate de eliminar de tu proyecto esta información de ayuda.

- [ ] **Identificar claramente todos los componentes del Software** que utilices a alto nivel. Deberías apoyarlo de un diagrama que represente la infraestructura necesaria para que opere tu software. Por ejemplo y como mínimo: Frontend, backend y base de datos. Software y/o infraestructura de soporte (On-premise, IaaS, PaaS...). Es decir, describe todo lo necesario para que tu software funcione. También describe si has empleado un enfoque monolítico, de microservicios

- [ ] Describe tu solución **frontend**. Dedica un apartado para justificar qué tecnología has empleado y el porqué. Indica si te has apoyado o no en un framework justificando tu respuesta. Ideas de tecnologías para esto son Vanilla JavaScript o JS Puro; Typescript o otros transpiladores de lenguaje que permitan programar en Java, Python, Dart... Uso de motores de plantillas junto con js y/o frameworks: Angular, React, Vue...

- [ ] Describe tu solución **backend**. Dedica un apartado para justificar qué tecnología has empleado y el porqué. Indica si te has apoyado o no en un framework justificando tu respuesta. Ideas de tecnologías son: PHP, NodeJS, Python... Frameworks: Spring Boot, Jakarta EE, Flask, Express, koa...

- [ ] Describe tu solución de **persistencia de datos**: bases de datos y/o cachés y/o menejo de sesión (statefull, stateless). Apóyate en diagramas o esquemas junto la descripción de estos.

- [ ] Describe todos los **componentes de terceros** que has utilizado con su licencia de uso.

- [ ] Describe como has gestionado las indentidades en tu solución (**autenticación y autorización**). Si has utilizado sistemas federados para ello o no (Google, Microsoft, Facebook, Apple, Discord...).

- [ ] Describe el proceso de **despliegue de tu solución**. Esto extendería la información del README. Esto puede ser autodocumentado en tu propio codigo (en tus ficheros de scrips de aprovisionamiento, `dockerfile`, `docker-compose`, `vagrantfile`, o tecnología similar). Pero haz una referencia en esta documentación a todos esos ficheros.

- [ ] Asegúrate que has logrado crear una forma de poner en marcha tu solución en un **ambiente de desarrollo** (con por ejemplo con composer, npm, maven, gradle, pip o similar según la tecnología elegida).

- [ ] Implementa las **interfaces gráficas** de tu solución con interface intuitiva y/o ayudas contextuales (tooltips, asistentes, hints, doc integrada, etc.). Esto sería alternativo a crear un manual de usario.

- [ ] **Documenta el resto de interfaces (backend)**: Especificación de los endpoints (si usas OpenAPI/Swagger, adjuntar el fichero YAML/JSON o un enlace a la documentación generada.) Ejemplos de uso con curl. Señalar autenticación/autorización (ej.: JWT, API Key).

- [ ] **Apóyate en un lenguaje de modelado** (UML) para describir gráficamente los aspectos más complejos de tu diseño. Preferiblemente, utiliza [MermaId](https://mermaid.js.org/) para generar estos diagramas que pueden ser de diferentes tipos. En su defecto, y si necesitas incluir otros diagramas generados con otras herramientas, expórtalos a png o jpeg para incluirlos en tu documentación.

- [ ] **Ubica las imágenes que necesites en la documentación** de tu proyecto en la carpeta `doc/img/` y léete el fichero [`doc/img/leame.txt`](img/leame.txt). **Otras imágenes** del proyecto (ajenas a la documentación) ubícalas donde corresponda según el framework o tu diseño.

- [ ] Describe **cómo has gestionado los tiempos y cómo has estimado el coste de tu solución**. Calcula y/o estima su impacto: Puede ser económico o por ejemplo una estimación de potenciales usuarios de la misma (sin necesariamente haber un retorno económico). También puede ser para un fin social. Si tu solución no es completa, identifica qué aspectos son necesarios seguir desarrollándola para tener dicho impacto.

- [ ] Describe **cómo te has organizado** en el desarrollo de tu proyecto. Cómo has identificado tareas y subtareas. Cómo se ha realizado el **seguiento de estas tareas**. Esta pequeña planificación de tu proyecto se puede adaptar al uso de marcos de desarrollo ágil, como Scrum, basados en el desarollo incremental en base a ciclos o iteraciones relativamente cortas.

Cosas que debes tener en cuenta:

- [ ] **No debes** presentar nada que no figure en esta documentación. Tu presentación será una síntesis de lo más relevante o a lo que le quieras dar más peso de tu proyecto. Te recomiendo autocitarte durante tu presentación para que el tribunal pueda tener referencia de lo que le hablas en esta documentación.

- [ ] Utiliza un lenguaje técnico pero al mismo tiempo **un lenguaje natural, claro y comprensible**. El propósito es que se entienda. Evita meter "*paja*" o cuestiones grandielocuentes que no le aporten valor, que no sean mesurables o demostrables. Evita futuribles.

- [ ] **Coordínate con tu tutor para tener claro los objetivos** de la próxima reunión. Podrías como sugerencia, identificar tus tareas en forma de "*issues*" en gitlab y montar un tablero kanban con cuatro columnas: "*Pendientes*", "*Trabajando en ello*", "*Para revisar con mi tutor*", "*Resueltas*". Esto es un seguimiento de tu proyecto y está dentro de los objetivos de este módulo del PFC. Úsalo como herramienta para avanzar pero también exponlo como logro si consigues realizar un buen seguimiento de tu avance.
