import React from 'react';
import { renderToString } from 'react-dom/server';
import Contacto from './Contacto';

describe('Contacto page', () => {
  test('incluye el formulario de contacto y el botón enviar', () => {
    const html = renderToString(<Contacto />);
    expect(html).toContain('Contacto');
    expect(html).toContain('Enviar mensaje');
    expect(html).toContain('Nombre');
    expect(html).toContain('Email');
  });
});
