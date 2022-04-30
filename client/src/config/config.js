export const config = {
	isDocker: process.env.REACT_APP_isDocker === "1" || false,
	dockerHost: process.env.REACT_APP_dockerHost || "localhost",
	dockerPort: process.env.REACT_APP_dockerPort || "9999",
	dockerPrefix: process.env.REACT_APP_dockerPrefix || "/api/",
	protocol: process.env.REACT_APP_protocol || "http",
	host: process.env.REACT_APP_host || "localhost",
	port: process.env.REACT_APP_port || "9000",
	prefix: process.env.REACT_APP_prefix || "/",
};
export const url = `${config.protocol}://${
	config.isDocker
		? `${config.dockerHost}:${config.dockerPort}${config.dockerPrefix}`
		: `${config.host}:${config.port}${config.prefix}`
}`;
console.log(url);
