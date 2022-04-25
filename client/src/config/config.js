export const config = {
	isDocker: process.env.isDocker === "1" || false,
	dockerHost: process.env.dockerHost || "localhost",
	dockerPort: process.env.dockerPort || "9999",
	dockerPrefix: process.env.dockerPrefix || "/api",
	protocol: process.env.protocol || "http",
	host: process.env.host || "localhost",
	port: process.env.port || "9000",
	prefix: process.env.prefix || "/",
};
export const url = `${config.protocol}://${
	config.isDocker
		? `${config.dockerHost}:${config.dockerPort}${config.dockerPrefix}`
		: `${config.host}:${config.port}${config.prefix}`
}`;
