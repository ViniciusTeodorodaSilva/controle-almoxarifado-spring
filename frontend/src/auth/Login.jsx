import { useState } from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from './AuthContext'
import Brand from '../components/Brand'
export default function Login(){
 const auth=useAuth(),location=useLocation()
 const from=location.state?.from
 const destination=typeof from==='string'&&from.startsWith('/')&&!from.startsWith('//')&&!from.startsWith('/login')?from:'/dashboard'
 const [username,setUsername]=useState(''),[password,setPassword]=useState(''),[busy,setBusy]=useState(false),[error,setError]=useState('')
 if(auth.user)return <Navigate to={destination} replace/>
 async function submit(event){event.preventDefault();if(busy)return;setBusy(true);setError('');try{await auth.login(username,password);setPassword('')}catch(e){setError(e.status===429?'Muitas tentativas. Tente novamente mais tarde.':e.status===401?'Usuário ou senha inválidos.':'Não foi possível entrar. Tente novamente.');setPassword('')}finally{setBusy(false)}}
 return <main className="login-page"><section className="login-panel"><div className="login-brand"><Brand variant="full"/><p>Plataforma BES</p></div><h1>Entrar</h1><form onSubmit={submit} aria-busy={busy}><label htmlFor="login-user">Usuário</label><input id="login-user" autoComplete="username" required maxLength={80} value={username} onChange={e=>setUsername(e.target.value)} disabled={busy} aria-describedby={error?'login-error':undefined}/><label htmlFor="login-password">Senha</label><input id="login-password" type="password" autoComplete="current-password" required value={password} onChange={e=>setPassword(e.target.value)} disabled={busy} aria-describedby={error?'login-error':undefined}/>{error&&<p id="login-error" className="login-error" role="alert">{error}</p>}<button className="btn" disabled={busy||auth.loading}>{busy?'Entrando…':'Entrar'}</button></form></section></main>
}
