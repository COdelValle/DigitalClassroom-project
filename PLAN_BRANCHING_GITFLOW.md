# Plan de Branching GitFlow - DigitalClassroom

**Versión**: 1.0.0  
**Fecha**: 19 de mayo de 2026  
**Equipo**: 2 personas  
**Ambiente**: Académico

---

## 📋 Introducción

Este documento define la estrategia de branching **GitFlow** adaptada a un equipo académico de 2 personas. GitFlow proporciona una estructura clara para el desarrollo, testing y despliegue de DigitalClassroom.

---

## 🌳 Estructura de Branches

```
main (producción) ←────────────────────────────────┐
 ├─ hotfix-* (correcciones urgentes)               │
 │   └─────→ develop →→→→→→→→→→→→→→→→→→→→→→→→→→→→┘
 │
develop (integración)
 ├─ feature-* (nuevas características)
 ├─ bugfix-* (corrección de bugs)
 └─ release-* (preparación de versión)
```

### Branches Principales

#### 1. **main** (Producción)

**Propósito**: Código en producción, estable y testeado

**Características**:
- ✅ Siempre listo para desplegar
- ✅ Protegido: requiere Pull Request
- ✅ Bloqueado: no se puede hacer push directo
- ✅ Etiquetado con versiones (v1.0.0)

**Regla**: Solo merge desde `release-*` o `hotfix-*`

```bash
# Ver versiones en main
git tag -l
```

#### 2. **develop** (Integración)

**Propósito**: Rama de integración donde convergen todas las features

**Características**:
- ✅ Refleja el estado en desarrollo
- ✅ Código compilable y funcional
- ✅ Tests pasando
- ✅ Protegido: requiere Pull Request

**Regla**: Base para todas las ramas de feature

```bash
# Actualizar develop local
git checkout develop
git pull origin develop
```

---

## 🔧 Tipos de Branches

### 1. Feature Branches (feature-*)

**Propósito**: Desarrollar nuevas características

**Nomenclatura**: `feature/<descripción-corta>`

**Ejemplos**:
```
feature/autenticacion-jwt
feature/gestion-estudiantes
feature/dashboard-profesor
feature/exportar-calificaciones
```

**Ciclo de Vida**:

```bash
# 1. Crear rama desde develop
git checkout develop
git pull origin develop
git checkout -b feature/autenticacion-jwt

# 2. Desarrollo
git add .
git commit -m "Impl: agregar autenticación JWT"
git commit -m "Feat: token refresh automático"

# 3. Push a repositorio
git push origin feature/autenticacion-jwt

# 4. Crear Pull Request (en GitHub/GitLab)
# - Title: "Feature: Autenticación JWT"
# - Description: explicar cambios
# - Asignar revisor (compañero de equipo)

# 5. Una vez aprobado: Merge
# 6. Eliminar rama local
git checkout develop
git pull origin develop
git branch -d feature/autenticacion-jwt
git push origin --delete feature/autenticacion-jwt
```

**Duración**: 1-7 días típicamente

**Regla de Merge**: Requiere:
- ✅ Al menos 1 review de código
- ✅ Todos los tests pasando
- ✅ Código libre de conflictos

---

### 2. Bugfix Branches (bugfix-*)

**Propósito**: Corregir bugs encontrados en desarrollo

**Nomenclatura**: `bugfix/<descripción-bug>`

**Ejemplos**:
```
bugfix/error-validacion-rut
bugfix/student-create-timeout
bugfix/calificaciones-negativas
```

**Diferencia con Feature**:
- Bug es un defecto/error
- Feature es funcionalidad nueva
- Mismo proceso que feature

```bash
git checkout -b bugfix/error-validacion-rut
# ... hacer cambios ...
git push origin bugfix/error-validacion-rut
# → Pull Request a develop
```

---

### 3. Release Branches (release-*)

**Propósito**: Preparar una versión nueva para producción

**Nomenclatura**: `release/v<versión>`

**Ejemplo**:
```
release/v1.0.0
release/v1.1.0
```

**Cuándo Crear**:
- Cuando features para esa versión están completas en develop
- Típicamente cada sprint o mes

**Ciclo de Vida**:

```bash
# 1. Crear rama release desde develop
git checkout develop
git pull origin develop
git checkout -b release/v1.0.0

# 2. Solo correcciones de bugs y versionado
# - Actualizar números de versión
# - Bugfixes críticos solo
# - NO nuevas features

# 3. Merge a main (producción)
git checkout main
git pull origin main
git merge --no-ff release/v1.0.0
git tag -a v1.0.0 -m "Version 1.0.0"
git push origin main
git push origin v1.0.0

# 4. Merge de vuelta a develop (con cambios de versión)
git checkout develop
git pull origin develop
git merge --no-ff release/v1.0.0
git push origin develop

# 5. Eliminar rama release
git branch -d release/v1.0.0
git push origin --delete release/v1.0.0
```

**Duración**: 2-5 días

---

### 4. Hotfix Branches (hotfix-*)

**Propósito**: Corregir bugs urgentes en producción

**Nomenclatura**: `hotfix/v<versión>-<descripción>`

**Ejemplo**:
```
hotfix/v1.0.0-datos-perdidos
hotfix/v1.0.1-crash-login
```

**Cuándo Usar**:
- Solo para bugs críticos en producción
- No puede esperar al siguiente release
- Afecta a muchos usuarios

**Ciclo de Vida**:

```bash
# 1. Crear desde main (producción)
git checkout main
git pull origin main
git checkout -b hotfix/v1.0.1-crash-login

# 2. Corregir bug
# ... hacer cambios ...

# 3. Mergear a main
git checkout main
git pull origin main
git merge --no-ff hotfix/v1.0.1-crash-login
git tag -a v1.0.1 -m "Hotfix v1.0.1"
git push origin main
git push origin v1.0.1

# 4. Mergear también a develop (para próxima versión)
git checkout develop
git pull origin develop
git merge --no-ff hotfix/v1.0.1-crash-login
git push origin develop

# 5. Eliminar hotfix
git branch -d hotfix/v1.0.1-crash-login
git push origin --delete hotfix/v1.0.1-crash-login
```

**Duración**: Horas a 1 día

---

## 📋 Convenciones de Commits

### Mensaje de Commit

**Formato**:
```
<tipo>(<scope>): <sujeto>

<descripción>

<footer>
```

### Tipos

```
feat:       Nueva característica
fix:        Corrección de bug
docs:       Cambios en documentación
style:      Cambios de formato (sin lógica)
refactor:   Reorganización de código
perf:       Mejora de rendimiento
test:       Agregar o actualizar tests
chore:      Cambios en build, deps, etc.
```

### Ejemplos

**Feature**:
```bash
git commit -m "feat(auth): implementar JWT token refresh"
```

**Bugfix**:
```bash
git commit -m "fix(student-manager): validar RUT correctamente"
```

**Documentación**:
```bash
git commit -m "docs(readme): agregar instrucciones de setup"
```

**Refactor**:
```bash
git commit -m "refactor(controller): extraer validación a service"
```

---

## 🔄 Flujo de Trabajo Típico (Equipo de 2)

### Escenario 1: Desarrollar Feature en Paralelo

**Persona 1**:
```bash
# Feature A: Autenticación
git checkout -b feature/autenticacion
# ... desarrollar ...
git push origin feature/autenticacion
# → Pull Request
```

**Persona 2**:
```bash
# Feature B: Dashboard
git checkout -b feature/dashboard
# ... desarrollar ...
git push origin feature/dashboard
# → Pull Request
```

**Revisor** (compañero):
```bash
# Person 1 revisa PR de Person 2
# Person 2 revisa PR de Person 1
# Feedback → Cambios → Merge
```

---

### Escenario 2: Hotfix en Producción

**Descubrimiento**: Bug en producción (main)

```bash
# Person 1 detecta bug
# Crea hotfix
git checkout main
git checkout -b hotfix/v1.0.1-error-critico

# ... corregir bug ...
git push origin hotfix/v1.0.1-error-critico

# Person 2 revisa rápidamente
# Merge a main
# Deploy a producción

# Merge a develop (para próxima versión)
```

---

### Escenario 3: Release Planning

**Semana N-1**:
```
develop tiene:
- feature/autenticacion ✓ merged
- feature/dashboard ✓ merged
- feature/reportes ✓ merged

Decidimos: Próxima versión será v1.1.0
```

**Semana N**:
```bash
# Person 1 crea release
git checkout -b release/v1.1.0

# Solo QA/bugfixes
# ... encontrar y corregir bugs ...

# Person 2 hace testing
# Todo OK → Merge a main → v1.1.0 tag
# Merge a develop
```

---

## 📊 Protecciones de Ramas

### main

```
✓ Requiere Pull Request
✓ Requiere 1 approval
✓ Requiere tests pasando
✓ No permitir push directo
✓ Merges: solo desde release-* y hotfix-*
```

### develop

```
✓ Requiere Pull Request
✓ Requiere 1 approval
✓ Requiere tests pasando
✓ No permitir push directo
✓ Merges: desde feature-*, bugfix-*, release-*, hotfix-*
```

### GitHub/GitLab Configuration

**En GitHub**:
1. Settings → Branches
2. Add rule:
   - Branch name pattern: `main`
   - Require pull request reviews: ✓
   - Require approval: 1
   - Require status checks: ✓
   - Dismiss stale reviews: ✓

---

## 🧪 Testing & Quality Gates

### Antes de Merge a develop

```bash
# 1. Ejecutar tests locales
npm run test              # Frontend
mvn test                  # Backend

# 2. Linting
npm run lint              # Frontend
mvn checkstyle:check      # Backend (si está configurado)

# 3. Build
npm run build             # Frontend
mvn clean package         # Backend

# 4. Cobertura mínima
npm run test:coverage     # Coverage > 70%
```

### CI/CD Check (Automático en PR)

```bash
# GitHub Actions / GitLab CI ejecuta:
✓ Tests
✓ Linting
✓ Build
✓ Code coverage
✓ Security scan

# No mergear hasta que todos pasen ✓
```

---

## 📈 Versioning

### Semver (Semantic Versioning)

**Formato**: `MAJOR.MINOR.PATCH`

```
v1.2.3
 │ │ └─ PATCH (0.0.1) bug fixes
 │ └──── MINOR (0.1.0) new features
 └────── MAJOR (1.0.0) breaking changes
```

### Ejemplos

- `v1.0.0` - Primera versión
- `v1.1.0` - Nuevas features (agosto)
- `v1.1.1` - Hotfix
- `v1.2.0` - Más features (septiembre)
- `v2.0.0` - Cambios breaking

### Archivo VERSION

Crear `/VERSION` en raíz:
```
1.0.0
```

Actualizar antes de release

---

## 🎓 Checklist para Merge

Antes de mergear PR:

```
□ Feature completada
□ Tests escritos y pasando
□ Code review aprobado
□ Sin conflictos
□ Documentación actualizada
□ Commits con mensajes claros
□ Ramas innecesarias eliminadas
□ No hay console.log() o debug
□ Performance aceptable
```

---

## 📞 Comunicación del Equipo

### Daily Stand-up (Sugerencia)

**Qué está haciendo**:
- Person 1: "feature/dashboard - 80% hecho"
- Person 2: "bugfix/timeout - ready para review"

**Bloqueos**:
- "Espera review de Person X"
- "Necesita decisión arquitectura"

**Próximos pasos**:
- "Mañana: mergear feature/auth a develop"

---

## 🚀 Despliegue

### Desarrollo (local)
```bash
git checkout develop
npm run dev / mvn spring-boot:run
```

### Staging (staging branch si existe)
```bash
git checkout staging
# Desplegar a servidor staging
```

### Producción (main)
```bash
git checkout main
# Tag: v1.0.0
# Build docker image
# Deploy
```

---

## 🐛 Troubleshooting

### Resolver Conflicto en PR

```bash
# 1. Traer develop actualizado
git checkout develop
git pull origin develop

# 2. Rebase feature en develop
git checkout feature/mi-feature
git rebase develop

# 3. Resolver conflictos
# Editar archivos conflictivos
git add .
git rebase --continue

# 4. Push forzado (solo en tu rama)
git push origin feature/mi-feature -f
```

### Deshacer Merge

```bash
# Si merge fue a develop y no tiene problemas
git revert -m 1 <commit-hash>
git push origin develop
```

---

## 🎯 Conclusión

Este plan GitFlow proporciona:

- ✅ Estructura clara para el equipo
- ✅ Control de cambios
- ✅ Trazabilidad de versiones
- ✅ Ambiente académico profesional
- ✅ Fácil colaboración en paralelo

---

**Última actualización**: 19 de mayo de 2026 ✓