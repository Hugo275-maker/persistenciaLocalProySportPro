# SportPro

SportPro - App móvil de gestión de academias deportivas (Desarrollo de Aplicaciones Móviles - Universidad ESAN)

## Stack

- Kotlin + Jetpack Compose (Material 3)
- Arquitectura MVVM: `ViewModel` + `StateFlow` + Navigation Compose
- Firebase: Authentication, Cloud Firestore (persistencia offline habilitada), Cloud Storage, Cloud Messaging
- Persistencia local con Room (SQLite): caché de la biblioteca de ejercicios
- Inyección de dependencias con Hilt

## Requisitos

- Android Studio (verificado con la serie 2026.1.x del equipo; si al abrir el proyecto Android
  Studio ofrece actualizar el Android Gradle Plugin / Gradle Wrapper, es seguro aceptar).
- JDK 17.
- El repositorio incluye el Gradle Wrapper: ejecuta `./gradlew assembleDebug` (o `gradlew.bat` en
  Windows) y `./gradlew testDebugUnitTest` para las pruebas unitarias.

## Firebase

El proyecto Firebase (`sportpro-198a4`) ya existe y `app/google-services.json` ya está
incluido en el repo con la app Android registrada (`com.esan.sportpro`). Servicios habilitados:

- Authentication (Email/Password)
- Cloud Firestore (activar reglas de seguridad por colección antes de manejar datos reales)
- Cloud Storage
- Cloud Messaging

Si necesitas acceso de administrador a la consola de Firebase, pídele al dueño del proyecto
que te agregue en **Configuración del proyecto → Usuarios y permisos**.

## Estructura de carpetas

```
app/src/main/java/com/esan/sportpro/
├── core/            # theme, ViewModel base, utilidades compartidas
├── di/              # módulos Hilt (Firebase, Room, etc.)
├── data/local/       # base de datos Room (SQLite): entidades y DAO
├── navigation/       # rutas y NavHost raíz
├── messaging/        # FirebaseMessagingService
├── data/<modulo>/     # DTOs, fuentes remotas (Firestore/Storage), repos
├── domain/<modulo>/   # modelos de dominio, casos de uso, interfaces de repo
└── ui/<modulo>/       # pantallas Compose + ViewModels del módulo
```

Módulos (`<modulo>`): `cuentas`, `academia`, `jugadores`, `entrenamientos`, `partido`,
`comunidad`, `ia`. Cada integrante trabaja principalmente dentro de su propia carpeta de
módulo en `data/`, `domain/` y `ui/`; los únicos archivos compartidos que pueden generar
conflicto son `navigation/SportProNavHost.kt`, `navigation/NavRoutes.kt` y los módulos en
`di/` — al tocarlos, agrega solo tu propia entrada y avisa en el PR.

## Persistencia local (Room & SQLite)

La biblioteca de ejercicios (US-009) guarda una copia local en SQLite usando Room, para que el
entrenador pueda ver y buscar ejercicios sin conexión.

| Archivo | Rol |
|---|---|
| `data/local/EjercicioEntity.kt` | `@Entity` de la tabla `ejercicios_biblioteca` y conversión a `EjercicioBiblioteca` |
| `data/local/EjercicioDao.kt` | `@Dao` con las consultas SQL (observar por academia, upsert, activar/desactivar, eliminar, reemplazar en una transacción) |
| `data/local/SportProDatabase.kt` | `@Database` `sportpro.db`, versión 1 |
| `di/DatabaseModule.kt` | Provee la base y el DAO con Hilt |
| `data/entrenamientos/FirestoreEntrenamientoRepository.kt` | Sincroniza Firestore → Room y escribe en ambos |

**Flujo de datos**

1. La pantalla observa `EntrenamientoRepository.observarBiblioteca()`, que lee de Room como `Flow`.
2. Mientras se observa, un listener de Firestore copia los documentos de `ejercicios` a la tabla
   (`reemplazarAcademia`, en una sola transacción). Room vuelve a emitir y la UI se actualiza.
3. Crear, editar, activar/desactivar o eliminar un ejercicio escribe en Firestore y en Room al
   mismo tiempo, así que el cambio se ve al instante aunque no haya red. Firestore lo envía al
   servidor cuando vuelve la conexión.
4. Sin conexión se muestran los datos guardados en el teléfono. Una caché vacía de Firestore no
   borra la copia local, y el error solo se muestra si no hay nada guardado.

Como la tabla es una copia de Firestore, un cambio de esquema sin migración recrea la base
(`fallbackToDestructiveMigration`) y se vuelve a llenar en la siguiente sincronización.

**Cómo probarlo**

1. Abre la Biblioteca de ejercicios con internet.
2. Activa el modo avión, cierra la app y vuelve a abrirla: los ejercicios siguen apareciendo.
3. En Android Studio, **App Inspection → Database Inspector** muestra la tabla
   `ejercicios_biblioteca` dentro de `sportpro.db`.
4. Prueba unitaria: `app/src/test/java/com/esan/sportpro/data/EjercicioEntityTest.kt`
   (`./gradlew testDebugUnitTest`).

## Convención de ramas

```
feature/US-0XX-nombre-corto
```

El número coincide con el issue de GitHub de la historia de usuario correspondiente
(`US-001` a `US-030`). Ejemplo: `feature/US-014-programacion-partido`. Cada PR debe
referenciar `Closes #<numero>` para cerrar el issue al mergear.

## Historias de usuario

Las 30 historias de usuario están documentadas como GitHub Issues (`US-001` a `US-030`),
etiquetadas por rol (Administrador, Entrenador, Jugador, Padre de familia).

## Estado del proyecto (entrega semana 7)

| Módulo | Historias | Estado |
|---|---|---|
| Cuentas: registro con rol, login, recuperar contraseña, sesión persistente, cierre de sesión | US-001, US-002 | Implementado |
| Navegación principal por rol | US-003 | Implementado: barra de 4 pestañas por rol, Inicio con resumen y "Más" (`MenuPrincipal`) |
| Academia: equipos por categoría | US-004, US-005 | Implementado (alta, edición, desactivar) |
| Jugadores: perfil con datos físicos, contacto y emergencia | US-006 | Base implementada (sin fotografía); faltan criterios de aceptación |
| Jugadores: vinculación padre–jugador (código, solicitudes, aprobación, revocación con auditoría) | US-007 | Implementado (notificación push pendiente de US-027) |
| Entrenamientos: biblioteca de ejercicios | US-009 | Implementado (ficha completa, buscador con filtros, permisos por autor, desactivación) |
| Entrenamientos: registro de asistencia (sin conexión, historial de cambios, cierre con % del equipo) | US-011 | Implementado |
| Entrenamientos: planificación e historial del jugador | US-010, US-012 | Base implementada; faltan criterios de aceptación |
| Partido en vivo, eventos y correcciones | — | Implementado (ver `docs/diseno-tecnico-partido-en-vivo.md`) |
| Mensualidades, Estadísticas, Anuncios, Comunidad | US-008, US-026 a US-030 | Implementado |
| Resumen narrativo con IA | — | Diseño listo (`docs/diseno-integracion-ia.md`); implementación en semana 12 |

## Documentación técnica

- [`docs/diseno-tecnico-partido-en-vivo.md`](docs/diseno-tecnico-partido-en-vivo.md): registro de eventos en tiempo real, offline y trazabilidad.
- [`docs/diseno-integracion-ia.md`](docs/diseno-integracion-ia.md): integración segura con el proveedor de IA.
- [`docs/modelo-datos-gestion.md`](docs/modelo-datos-gestion.md): colecciones de equipos, jugadores y entrenamientos, permisos por rol, criterio de fechas y regla del apoderado.
- [`docs/guia-estilo-ui.md`](docs/guia-estilo-ui.md): línea gráfica común (tema, componentes, navegación) y lista de revisión para los PR.
- `firestore.rules`: reglas de seguridad. Copiarlas en Firebase Console → Firestore Database → Reglas → Publicar.
