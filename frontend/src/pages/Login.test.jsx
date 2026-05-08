import React from 'react';
import { renderToString } from 'react-dom/server';
import { MemoryRouter } from 'react-router-dom';
import Login from './Login';

describe('Login page', () => {
  test('muestra el formulario de inicio de sesión', () => {
    const html = renderToString(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );
    expect(html).toContain('Iniciar Sesión');
    expect(html).toContain('RUT');
    expect(html).toContain('Contraseña');
  });
});
