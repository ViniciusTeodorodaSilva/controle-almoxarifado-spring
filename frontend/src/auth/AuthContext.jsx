import { createContext, useContext, useEffect, useState } from 'react'
import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { request, resetCsrf, csrf } from '../api/client'
import { can, routePermission } from './permissions'
import { LoadingState, Notice } from '../components/ui'
const Context = createContext(null)
export function AuthProvider({children}) {
 const [user,setUser]=useState(null), [loading,setLoading]=useState(true), [error,setError]=useState('')
 useEffect(()=>{
  let live=true
  const expired=()=>{resetCsrf();setUser(null)}
  window.addEventListener('bes:session-expired',expired)
  request('/auth/me').then(value=>{if(live)setUser(value)}).catch(e=>{if(live&&e.status!==401)setError('Não foi possível verificar sua sessão. Tente novamente.')}).finally(()=>{if(live)setLoading(false)})
  return()=>{live=false;window.removeEventListener('bes:session-expired',expired)}
 },[])
 async function login(username,password){await request('/auth/login',{method:'POST',body:{username,password}});resetCsrf();await csrf();const value=await request('/auth/me');setUser(value);setError('')}
 async function logout(){await request('/auth/logout',{method:'POST'});resetCsrf();setUser(null)}
 return <Context.Provider value={{user,loading,error,login,logout,can:p=>can(user,p)}}>{children}</Context.Provider>
}
export const useAuth=()=>useContext(Context)
export function Can({permission,children}){const auth=useAuth();return auth?.can(permission)?children:null}
export function Protected(){const auth=useAuth(),location=useLocation();if(auth.loading)return <LoadingState/>;if(auth.error)return <div className="state"><Notice error>{auth.error}</Notice><button className="btn" onClick={()=>window.location.reload()}>Tentar novamente</button></div>;if(!auth.user)return <Navigate to="/login" state={{from:location.pathname+location.search}} replace/>;const key=Object.keys(routePermission).find(path=>location.pathname===path||location.pathname.startsWith(path+'/'));if(key&&!auth.can(routePermission[key]))return <div className="state"><h1>Acesso restrito</h1><p>Você não tem permissão para acessar esta página.</p></div>;return <Outlet/>}
