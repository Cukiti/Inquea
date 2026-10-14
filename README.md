# Inquea 📍

> ⏸️ **Proyecto en desarrollo, en pausa hasta nuevo aviso.**
> El código refleja el estado actual del trabajo. Algunas funciones pueden estar incompletas.

**Marketplace Android de servicios locales: los negocios publican reels y ofertas flash, y los clientes descubren, reservan y pagan desde la app.**

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)
![Hilt](https://img.shields.io/badge/DI-Hilt-34A853)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?logo=firebase&logoColor=black)
![Estado](https://img.shields.io/badge/estado-en%20pausa-lightgrey)

## Qué es

Una app con dos roles:

- **Negocio**: configura su perfil, sube videos cortos (reels) de su trabajo, crea ofertas flash por tiempo limitado y gestiona su agenda de citas.
- **Cliente**: descubre negocios en un feed de video y en un radar de búsqueda por cercanía, reserva citas, paga y conversa con el negocio por chat.

## Funciones implementadas

- Registro, inicio de sesión y selección de rol.
- Feed de reels con reproducción de video (Media3 / ExoPlayer).
- Ofertas flash y agenda de reservas del negocio.
- Reservas, pagos y centro de resolución del cliente.
- Chat en tiempo real entre cliente y negocio.

## Aspectos técnicos

- **Clean Architecture** (capas `data` / `domain` / `ui`) con **MVVM**.
- Inyección de dependencias con **Hilt**.
- **Firebase**: Auth, Firestore y Storage, con reglas de seguridad propias.
- UI en **Jetpack Compose** con Navigation y Coil.

## Compilar

1. Coloca tu `google-services.json` en `app/`.
2. Ejecuta `./gradlew assembleDebug`.

---
Desarrollado por **[Cukiti](https://github.com/Cukiti)**.
