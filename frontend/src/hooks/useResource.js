import { useEffect, useState } from 'react'
export function useResource(loader) {
  const [state, setState] = useState({ data: null, loading: true, error: null })
  const [version, setVersion] = useState(0)
  useEffect(() => {
    const controller = new AbortController()
    setState(previous => ({ ...previous, loading: true, error: null }))
    loader(controller.signal).then(data => {
      if (!controller.signal.aborted) setState({ data, loading: false, error: null })
    }).catch(error => {
      if (!controller.signal.aborted) setState({ data: null, loading: false, error })
    })
    return () => controller.abort()
  }, [loader, version])
  return { ...state, reload: () => setVersion(value => value + 1) }
}
